package fr.uge.gitclout.database.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileTypeTest {

    @Test
    void enumSimpleTest() {

        assertAll(
            () -> assertEquals("CODE", FileType.CODE.name()),
            () -> assertEquals("BUILD", FileType.BUILD.name()),
            () -> assertEquals("CONFIGURATION", FileType.CONFIGURATION.name()),
            () -> assertEquals("RESSOURCE", FileType.RESSOURCE.name()),
            () -> assertEquals("DOCUMENTATION", FileType.DOCUMENTATION.name())
        );
    }
}