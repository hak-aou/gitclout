package fr.uge.gitclout.database.entities;

import jakarta.persistence.*;

@Entity(name = "Tag")
@Table (name = "TAG")
@NamedQueries({
    @NamedQuery(name = "getTagsByRepositoryId",
                query = "SELECT t FROM Tag t WHERE t.repository.repositoryId = :id"),
    @NamedQuery(name = "getTagByName",
                query = "SELECT t FROM Tag t WHERE t.name = :name"),
    @NamedQuery(name = "getTagById",
                query = "SELECT t FROM Tag t WHERE t.tagId = :id")
})
public class Tag {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="tag_id")
    private long tagId;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;

    @Column(length = 128)
    private String name;

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Tag:" + getName();
    }
}
