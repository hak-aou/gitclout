package fr.uge.gitclout.database.main;

import fr.uge.gitclout.database.entities.Repository;
import fr.uge.gitclout.database.entities.Tag;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GitClone {

    private final FileTypeManager fileTypeManager = new FileTypeManager();

    /**
     * Get the last word separate with '/'
     * @param string String of url or path
     * @return name of url and path
     */
    private String getNameOnly(String string) {
        var stringSplit = string.split("/");
        return stringSplit[stringSplit.length - 1];
    }


    /**
     * Create folder using url name and path destination
     * @param url Url of the git repository
     * @param path Path destination where to create folder
     * @return Folder path
     */
    public Path createFolder(String url, Path path) {
        /* Create new folder to clone the repository */
        Path folder = Paths.get(System.getProperty("user.dir"), path.toString(), getNameOnly(url));

        try {
            Files.createDirectories(folder);
        }catch (IOException e) {
            System.err.println("Error to create folder : " + e.getMessage());
        }
        return folder;
    }

    /**
     * Clone the repository into path. We use setBare to not clone all the files.
     * @param url Url of the git repository
     * @param path Path destination where to create folder
     * @return git object
     */
    public Git cloneRepository(String url, Path path) throws GitAPIException, IOException {
        if(Files.exists(path.resolve("logs"))) {
            System.out.println("Git repository already exist!");
            return Git.open(path.toFile());
        }

        /* Clone the git repository */
        System.out.println("Creation of the git repository");
        return Git.cloneRepository().setBare(true).setURI(url).setDirectory(path.toFile()).call();
    }

    /**
     * Initialize RevTree of given tag
     * @param git git object
     * @param tag tag object
     * @return revTree object
     */
    private RevTree initialisationTree(Git git, Ref tag) throws IOException {
        RevWalk revWalk = new RevWalk(git.getRepository());
        RevCommit commit = revWalk.parseCommit(tag.getObjectId());

        return commit.getTree();
    }

    /**
     * Function to travel the tree and retrieves all the path of a git without filter it.
     * @param tree tree object
     * @param git git object
     * @return List of paths
     */
    private List<Path> runThroughTree(RevTree tree, Git git)  throws IOException {
        ArrayList<Path> nonFilteredListPaths = new ArrayList<>();
        try (TreeWalk treeWalk = new TreeWalk(git.getRepository())) {
            treeWalk.addTree(tree); treeWalk.setRecursive(true);
            while (treeWalk.next()) {
                if(!treeWalk.isSubtree()) /* If it's not a folder */
                    nonFilteredListPaths.add(Paths.get(treeWalk.getPathString())); /* Extract file name from path */
            }
        }
        return nonFilteredListPaths;
    }

    /**
     * Retrieve all the path of a git without filter it.
     * @param git git object
     * @param tag tag object
     * @return List of paths
     */
    private List<Path> nonFilteredPath(Git git, Ref tag) throws IOException {
        RevTree tree = initialisationTree(git, tag);
        return runThroughTree(tree, git);
    }

    /**
     *  Filter the list of path to get only useful path
     * @param allPaths allPaths object
     * @return List of paths
     */
    public List<Path> filteredPath(List<Path> allPaths) {
        var listExtensions = fileTypeManager.listAllExtensions();
        return allPaths.stream()
                .filter(path -> listExtensions.contains(fileTypeManager.getExtension(path.toFile())))
                .toList();
    }

    /**
     * Make a git blame on a list of file
     * @param git Git object
     * @param filteredListPaths list of file path to blame
     * @param tag tag object
     * @return ContributorManager object
     */
    private ContributorManager blameFiles(Git git, List<Path> filteredListPaths, Ref tag) throws IOException, GitAPIException {
        var contributors = new ContributorManager();
        var tagObjectId = tag.getObjectId();

        for(var path: filteredListPaths) {
            BlameCommand blameCommand = git.blame().setStartCommit(tagObjectId);
            contributors.blame(path, blameCommand, fileTypeManager);
        }

        return contributors;
    }

    /**
     * Analyze multiple tags with a git blame and add data in tag manager
     * @param tagManager tagManager object
     * @param git git object
     * @param tag tag object
     */
    private void analyzeTags(TagManager tagManager, Git git, Ref tag) {
        try {
            var listPaths = nonFilteredPath(git, tag);                  /* Parse all paths and get only useful */
            var filteredListPaths = filteredPath(listPaths);

            var contributors = blameFiles(git, filteredListPaths, tag); /* Blame file */
            tagManager.add(new Tag(getNameOnly(tag.getName())), contributors); /* Adding data */
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Analyze only one tag
     * @param tag tag object
     */
    public static void analyzeSpecificTag(Tag tag) throws GitAPIException, IOException {
        System.out.println("Analyze tag : " + tag.getName());

        var repository = tag.getRepository();
        var url = repository.getUrl();

        GitClone gitClone = new GitClone();

        var path = gitClone.createFolder(url, Path.of("/src/main/java/repository/"));
        var git = gitClone.cloneRepository(url, path);

        var ref = git.getRepository().findRef(tag.getName());
        if (ref == null) {
            System.err.println("Did not find tag named : " + tag.getName());
        }

        TagManager tagManager = new TagManager();
        gitClone.analyzeTags(tagManager, git, ref);

        var database = new DataBaseManager();

        database.insertRepository(tag.getRepository());
        database.insertAll(tagManager, tag.getRepository());
        System.out.println("Finish Analyze tag");
    }

    /**
     * Analyze all the tags in a git repository
     * @param git git object
     * @return tagManager object
     */
    public TagManager checkoutAndBlame(Git git) throws GitAPIException {
        TagManager tagManager = new TagManager();

        /* For each file we do git blame and retrieve information */
        for(var tag : git.tagList().call().reversed()) {
            analyzeTags(tagManager, git, tag);
        }

        return tagManager;
    }

    /**
     * Analyze a git repository
     * @param url Url of the git repository
     */
    public static void analyzeRepository(String url) throws GitAPIException, IOException {
        GitClone gitClone = new GitClone();

        var path = gitClone.createFolder(url, Path.of("/src/main/java/repository/"));
        var git = gitClone.cloneRepository(url, path);

        insertDataToDatabase(gitClone, git, url);
    }

    /**
     * Insert the analyzed data from a git repository into the database
     * @param gitClone gitClone object
     * @param git git object
     * @param url Url of the git repository
     */
    private static void insertDataToDatabase(GitClone gitClone, Git git, String url) throws GitAPIException {
        var tagManager = gitClone.checkoutAndBlame(git);

        var database = new DataBaseManager();
        var repository = new Repository(url, new Date());

        database.insertRepository(repository);
        database.insertAll(tagManager, repository);
        System.out.println(tagManager);
    }

    /**
     * Return all tags of a remote git repository
     * @param repository repository object
     * @return Collection of ref (tag)
     */
    private static Collection<Ref> getAllTags(Repository repository) {
        var url = repository.getUrl();
        LsRemoteCommand remote = Git.lsRemoteRepository()
                .setTags(true)
                .setRemote(url);
        try {
            Collection<Ref> remoteRefs = remote.call();

            return remoteRefs.stream()
                    .sorted(Comparator.comparing(Ref::getName))
                    .collect(Collectors.toList());
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insert tags of a remote git repository into the database
     * @param url Url of the git repository
     */
    public static void insertTags(String url) throws GitAPIException, IOException {
        var database = new DataBaseManager();
        var repository = new Repository(url, new Date());

        database.insertRepository(repository);
        var tagList = getAllTags(repository);
        database.insertAllTags(tagList, repository);
    }
}