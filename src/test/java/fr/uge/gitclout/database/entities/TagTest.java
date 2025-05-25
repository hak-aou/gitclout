package fr.uge.gitclout.database.entities;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    private final Tag tag = new Tag();

    @Test
    void idTest() {
        tag.setTagId(100L);
        assertEquals(100L, tag.getTagId());
    }

    @Test
    void nameTest() {
        tag.setName("v1.O.O");
        assertEquals("v1.O.O", tag.getName());
    }

    @Test
    void repositoryTest() {
        Repository repository = new Repository();
        tag.setRepository(repository);
        assertEquals(repository, tag.getRepository());
    }
}