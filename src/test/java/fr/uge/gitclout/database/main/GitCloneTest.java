package fr.uge.gitclout.database.main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GitCloneTest {

    private final GitClone gitClone = new GitClone();

    @Test
    void filteredPathTest() {
        Path path1 = Paths.get("Contributor.java");
        Path path2 = Paths.get("game.exe");
        Path path3 = Paths.get("README.md");
        Path path4 = Paths.get("model.obj");
        Path path5 = Paths.get("image.png");
        Path path6 = Paths.get("projet_java.zip");

        List<Path> allPaths          = Arrays.asList(path1, path2, path3, path4, path5, path6);
        List<Path> filteredListPaths = Arrays.asList(path1, path3, path5);
        assertEquals(filteredListPaths, gitClone.filteredPath(allPaths));
    }

}