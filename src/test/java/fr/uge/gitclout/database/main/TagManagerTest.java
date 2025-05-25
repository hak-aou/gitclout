package fr.uge.gitclout.database.main;

import fr.uge.gitclout.database.entities.Tag;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class TagManagerTest {

    private final TagManager tagManager = new TagManager();

    @Test
    void addTest() {
        var tag = new Tag("v1.0.0");;
        var contributorManager = new ContributorManager();

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> tagManager.add(tag, null)),
                () -> assertThrows(NullPointerException.class, () -> tagManager.add(null, contributorManager)),

                () -> tagManager.add(tag, contributorManager),
                () -> assertEquals("{Tag:v1.0.0=[]} SIZE = 1", tagManager.toString())
        );
    }

    @Test
    void getTagManager() {
        var tag = new Tag("v1.0.0");;
        var contributorManager = new ContributorManager();

        LinkedHashMap<Tag, ContributorManager> tagManagerTmp = new LinkedHashMap<>();;
        tagManagerTmp.put(tag, contributorManager);

        tagManager.add(tag, contributorManager);
        assertEquals(tagManagerTmp, tagManager.getTagManager());
    }

    @Test
    void testToStringTest() {
        var tag1 = new Tag("v1");
        var tag2 = new Tag("v2");

        tagManager.add(tag1, new ContributorManager());
        tagManager.add(tag2, new ContributorManager());

        assertEquals("{Tag:v1=[], Tag:v2=[]} SIZE = 2", tagManager.toString());
    }
}