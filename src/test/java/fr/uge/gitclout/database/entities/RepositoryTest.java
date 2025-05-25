package fr.uge.gitclout.database.entities;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {
    private final Repository repository = new Repository();

    @Test
    void urlTest() {
        repository.setUrl("https://github.com/ilyagru/Space-Snake");
        assertEquals("https://github.com/ilyagru/Space-Snake", repository.getUrl());
    }

    @Test
    void idTest() {
        repository.setRepositoryId(100L);
        assertEquals(100L, repository.getRepositoryId());
    }

    @Test
    void dateTest() {
        var date = new Date();
        repository.setDate(date);
        assertEquals(date, repository.getDate());
    }
}