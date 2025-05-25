package fr.uge.gitclout.database.main;

import fr.uge.gitclout.database.entities.Contributor;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ContributorManager {
    private final HashSet<Contributor> listContributors;

    /**
     * Convert hashSet to list
     * @return List of contributors
     */
    public List<Contributor> getList() {
        return List.copyOf(listContributors);
    }

    public ContributorManager() {
        this.listContributors = new HashSet<>();
    }

    /**
     * Check if we have contributor with a certain mail in the HashSet
     * @param mail email of contributor
     * @return true or false
     */
    public boolean contains(String mail) {
        Objects.requireNonNull(mail);
        return listContributors.stream().map(Contributor::getMail).collect(Collectors.toSet()).contains(mail);
    }

    /**
     * Return the contributor with a certain mail and name.
     * If contributor don't exist in our HashSet we create it.
     * @param mail email of contributor
     * @param name name of contributor
     * @return New or old contributor in list
     */
    public Contributor getContributorByMail(String mail, String name) {
        Objects.requireNonNull(mail);
        Objects.requireNonNull(name);

        for(var contributor: listContributors) {
            if(contributor.getMail().equals(mail)) {
                return contributor;
            }
        }
        return addContributor(mail, name); // if contributor do not exist so we create it
    }

    /**
     * Create and add contributor in our set
     * @param mail email of contributor
     * @param name name of contributor
     * @return contributor
     */
    public Contributor addContributor(String mail, String name) {
        Objects.requireNonNull(mail);
        Objects.requireNonNull(name);

        var contributor = new Contributor(mail, name);
        listContributors.add(contributor);
        return contributor;
    }

    @Override
    public String toString() {
        return listContributors.toString();
    }

    /**
     * Increment the number of line for a specific type of files.
     * @param contributor the contributor to update
     * @param type can be CODE BUILD CONFIGURATIONS RESSOURCE DOCUMENTATIONS
     */
    public void addLine(Contributor contributor, FileType type) {
        Objects.requireNonNull(type);
        switch(type) {
            case CODE          -> contributor.setCodesLines(contributor.getCodesLines()         + 1);
            case BUILD         -> contributor.setBuildsLines(contributor.getBuildsLines()       + 1);
            case CONFIGURATION -> contributor.setConfigsLines(contributor.getConfigsLines()     + 1);
            case RESSOURCE     -> contributor.setResourcesLines(contributor.getResourcesLines() + 1);
            case DOCUMENTATION -> contributor.setDocsLines(contributor.getDocsLines()           + 1);
        }
    }

    /**
     * Function to check if we can add line in a code file.
     * @param insideComments Variable to know if we are already in comments.
     * @param extension extension of the file (py, js, c ...)
     * @param line the line of code
     * @return true or false
     */
    private boolean checkConditionAdd(boolean insideComments, String extension, String line) {
        return switch (extension) {
            case "c", "js", "java":
                yield insideComments ? line.contains("*/") : !( line.startsWith("//") || line.startsWith("/*") );
            case "py":
                yield insideComments ? line.contains("\"\"\"") : !( line.startsWith("#") || line.startsWith("\"\"\"") );
            default:
                yield true;
        };
    }

    /**
     *  Return true if the file it's not code file because there are no comments
     * @param insideComments Variable to know if we are already in comments.
     * @param extension extension of the file (py, js, c ...)
     * @param line the line of code
     * @param type can be CODE BUILD CONFIGURATIONS RESSOURCE DOCUMENTATIONS
     * @return true of false
     */
    public boolean canAddLineInCodeFile(boolean insideComments, String extension, String line, FileType type) {
        if (type != FileType.CODE)
            return true;

        line = line.trim();
        return checkConditionAdd(insideComments, extension, line);
    }

    /**
     * Apply git blame on file and add line contributions to contributor.
     * @param path path of file
     * @param blameCommand blameCommand object
     * @param fileTypeManager fileTypeManager contains method to get extension of file
     */
    public void blame(Path path, BlameCommand blameCommand, FileTypeManager fileTypeManager) throws GitAPIException {
        BlameResult blame = blameCommand.setFilePath(path.toString()).call();
        RawText allText = blame.getResultContents();
        var typeFile = fileTypeManager.type(path.toFile());
        var extensions = fileTypeManager.getExtension(path.toFile());
        var insideComments = false;

        for(int line = 0; line < allText.size(); line++) {
            addLineCondition(insideComments, extensions, typeFile, allText, blame, line);
        }
    }

    /**
     * Return contributor using blame result.
     * @param blame BlameResult object
     * @param line number of the line
     * @return contributor
     */
    private Contributor getContributorByBlame(BlameResult blame, int line) {
        var author = blame.getSourceAuthor(line);
        var mail = author.getEmailAddress();
        var name = author.getName();
        return getContributorByMail(mail, name);
    }

    /**
     * Add line to the correct type file based on condition.
     * @param insideComments Variable to know if we are already in comments.
     * @param extensions extension of the file (py, js, c ...)
     * @param typeFile can be CODE BUILD CONFIGURATIONS RESSOURCE DOCUMENTATIONS
     * @param allText raw text
     * @param blame blameResult object
     * @param line the line of code
     */
    private void addLineCondition(boolean insideComments, String extensions, FileType typeFile, RawText allText, BlameResult blame, int line) {
        var canAdd = canAddLineInCodeFile(insideComments, extensions, allText.getString(line), typeFile);
        var contributor = getContributorByBlame(blame, line);
        if (canAdd) {
            addLine(contributor, typeFile);
        }
        if(!canAdd && typeFile == FileType.CODE) {
            addLine(contributor, FileType.DOCUMENTATION);
        }
    }
}