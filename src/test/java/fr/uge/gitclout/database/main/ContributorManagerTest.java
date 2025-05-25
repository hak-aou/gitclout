package fr.uge.gitclout.database.main;

import fr.uge.gitclout.database.entities.Contributor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ContributorManagerTest {

    private final ContributorManager contributorManager = new ContributorManager();

    @Test
    void getListAddContributorTest() {
        Contributor contributor = new Contributor("personne@gmail.com", "Personne");
        contributorManager.addContributor("personne@gmail.com", "Personne");

        assertEquals(contributor.getMail(), contributorManager.getList().getFirst().getMail());
    }

    @Test
    void containsTest() {
        Contributor contributor = new Contributor("personne@gmail.com", "Personne");
        contributorManager.addContributor("personne@gmail.com", "Personne");

        assertTrue(contributorManager.contains("personne@gmail.com"));
    }

    @Test
    void getContributorByMailTest() {
        Contributor contributor = new Contributor("personne@gmail.com", "Personne");
        contributorManager.addContributor("personne@gmail.com", "Personne");

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> contributorManager.getContributorByMail(null, "Personne")),
                () -> assertThrows(NullPointerException.class, () -> contributorManager.getContributorByMail("personne@gmail.com", null)),

                () -> assertEquals(contributor.getMail(), contributorManager.getContributorByMail("personne@gmail.com", "Personne").getMail()),
                () -> assertEquals(contributor.getName(), contributorManager.getContributorByMail("personne@gmail.com", "Personne").getName())
        );
    }

    @Test
    void addLineTest() {
        Contributor contributor = new Contributor("personne@gmail.com", "Personne");

        IntStream.range(0, 25).forEach(i -> {
            contributorManager.addLine(contributor, FileType.CODE);
        });

        IntStream.range(0, 12).forEach(i -> {
            contributorManager.addLine(contributor, FileType.BUILD);
        });
        assertAll(
            () -> assertEquals(25, contributor.getCodesLines()),
            () -> assertEquals(0, contributor.getDocsLines()),
            () -> assertEquals(12, contributor.getBuildsLines())

        );
    }

    @Test
    void commentsTest() {
        assertAll(
            () -> assertFalse(contributorManager.canAddLineInCodeFile(false, "py", "# This function return an integer", FileType.CODE)),
            () -> assertFalse(contributorManager.canAddLineInCodeFile(false, "py", "\"\"\" Param : @a @b", FileType.CODE)),
            () -> assertTrue(contributorManager.canAddLineInCodeFile(false, "py", "def function():", FileType.CODE)),
            () -> assertFalse(contributorManager.canAddLineInCodeFile(true, "py", "a, b = b, c", FileType.CODE)),

            () -> assertFalse(contributorManager.canAddLineInCodeFile(false, "c", "// Test function", FileType.CODE)),
            () -> assertFalse(contributorManager.canAddLineInCodeFile(true, "c", "int a = 23", FileType.CODE)),
            () -> assertTrue(contributorManager.canAddLineInCodeFile(false, "c", "int a = 23", FileType.CODE)),
            () -> assertTrue(contributorManager.canAddLineInCodeFile(true, "c", "finish comments */ int a = c", FileType.CODE)),
            () -> assertTrue(contributorManager.canAddLineInCodeFile(true, "py", "the comments is finish\"\"\" def function()", FileType.CODE))
        );
    }

}