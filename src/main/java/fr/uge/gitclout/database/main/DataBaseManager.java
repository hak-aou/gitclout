package fr.uge.gitclout.database.main;

import fr.uge.gitclout.database.entities.Contributor;
import fr.uge.gitclout.database.entities.Repository;
import fr.uge.gitclout.database.entities.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transactional;
import org.eclipse.jgit.lib.Ref;

import java.util.ArrayList;
import java.util.Collection;


public class DataBaseManager {

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("DataBaseGitClout");

    /**
     * Insert a repository into the database and associate it with a tag
     * @param tag To have the tag id
     * @param contributor Contributor to insert
     */
    @Transactional
    public void insertContributor(Tag tag, Contributor contributor) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        contributor.setTag(tag);
        transaction.begin();
        entityManager.merge(contributor);
        transaction.commit();
    }

    /**
     * Insert a repository into the database
     * @param repository Repository object
     */
    @Transactional
    public void insertRepository(Repository repository) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        entityManager.merge(repository);
        transaction.commit();
    }

    /**
     * Insert a tag into the database
     * @param tag Tag to insert
     * @param repository Repository to associate with a tag
     */
    @Transactional
    public void insertTag(Tag tag, Repository repository) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        tag.setRepository(repository);

        var tagTest = entityManager.createNamedQuery("getTagByName", Tag.class)
                                   .setParameter("name", tag.getName())
                                   .getResultList();

        if (tagTest.isEmpty()) {
            entityManager.persist(tag);
        } else {
            tag.setTagId(tagTest.getFirst().getTagId());
            entityManager.merge(tag);
        }
        transaction.commit();
    }

    /**
     * Insert all tags of the collection into the database
     * @param tagsList Collections of tags
     * @param repository Repository object
     */
    @Transactional
    public void insertAllTags(Collection<Ref> tagsList, Repository repository) {
        new ArrayList<>(tagsList).reversed().forEach((tag) ->
                insertTag(new Tag(tag.getName().substring("refs/tags/".length())), repository)
        );
    }

    /**
     * Insert the tag and the contributors of the tag manager.
     * @param tagManager tagManager object
     * @param repository Repository object
     */
    @Transactional
    public void insertAll(TagManager tagManager, Repository repository) {
        tagManager.getTagManager().forEach((tag, contributors) -> {
            insertTag(tag, repository);
            contributors.getList().forEach(contributor -> insertContributor(tag, contributor));
        });
    }
}