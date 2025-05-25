package fr.uge.gitclout.database.entities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContributorTest {

    private final Contributor contributor = new Contributor();

    @Test
    void mailTest() {
        contributor.setMail("personne@gmail.com");
        assertEquals("personne@gmail.com", contributor.getMail());
    }

    @Test
    void nameTest() {
        contributor.setName("Personne");
        assertEquals("Personne", contributor.getName());
    }

    @Test
    void codesLinesTest() {
        contributor.setCodesLines(100);
        assertEquals(100, contributor.getCodesLines());
    }
}