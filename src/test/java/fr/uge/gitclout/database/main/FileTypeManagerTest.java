package fr.uge.gitclout.database.main;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class FileTypeManagerTest {

    private final FileTypeManager fileTypeManager = new FileTypeManager();

    @Test
    void getValueConfigTest() {
        var codes          = fileTypeManager.getValueConfig("codes");
        var builds         = fileTypeManager.getValueConfig("builds");
        var config         = fileTypeManager.getValueConfig("configurations");
        var resources      = fileTypeManager.getValueConfig("resources");
        var documentations = fileTypeManager.getValueConfig("documentations");

        assertAll(
            () -> assertTrue(codes.containsAll(Set.of("c", "js", "java", "py")) ),
            () -> assertTrue(builds.contains("xml")),
            () -> assertTrue(config.containsAll(Set.of("github", "gitignore")) ),
            () -> assertTrue(resources.containsAll(Set.of("txt", "png", "jpg", "mp4"))),
            () -> assertTrue(documentations.contains("md"))
        );
    }

    @Test
    void typeTest() {
        File file1 = new File("./fr/uge/gitclout/database/api/EntityResource.java");
        File file2 = new File("./META-INF/persistence.xml");
        File file3 = new File(".gitignore");
        File file4 = new File("image.png");
        File file5 = new File("../../frontend/svelte-app/README.md");

        assertAll(
            () -> assertEquals(FileType.CODE,          fileTypeManager.type(file1)),
            () -> assertEquals(FileType.BUILD,         fileTypeManager.type(file2)),
            () -> assertEquals(FileType.CONFIGURATION, fileTypeManager.type(file3)),
            () -> assertEquals(FileType.RESSOURCE,     fileTypeManager.type(file4)),
            () -> assertEquals(FileType.DOCUMENTATION, fileTypeManager.type(file5))
        );
    }

    @Test
    void listAllExtensionsTest() {
        var allExtensions = fileTypeManager.listAllExtensions();
        assertAll(
            () -> assertTrue(allExtensions.contains("java")),
            () -> assertTrue(allExtensions.contains("xml")),
            () -> assertTrue(allExtensions.contains("md"))
        );
    }

    @Test
    void getExtensionTest() {
        File file1 = new File("Contributor.java");
        File file2 = new File("main.py");
        File file3 = new File("text.txt");
        File file4 = new File("image.png");
        File file5 = new File("user.pdf");
        File file6 = new File("README.md");
        File file7 = new File("persistence.xml");

        assertAll(
                () -> assertEquals("java", fileTypeManager.getExtension(file1)),
                () -> assertEquals("py",   fileTypeManager.getExtension(file2)),
                () -> assertEquals("txt",  fileTypeManager.getExtension(file3)),
                () -> assertEquals("png",  fileTypeManager.getExtension(file4)),
                () -> assertEquals("pdf",  fileTypeManager.getExtension(file5)),
                () -> assertEquals("md",   fileTypeManager.getExtension(file6)),
                () -> assertEquals("xml",  fileTypeManager.getExtension(file7))
        );
    }
}