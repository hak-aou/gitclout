package fr.uge.gitclout.database.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity(name = "Repository")
@Table (name = "REPOSITORY")
@NamedQueries({
        @NamedQuery(name = "getAllRepositories",
                    query = "SELECT r FROM Repository r"),
        @NamedQuery(name = "getRepositoryById",
                    query = "SELECT r FROM Repository r WHERE r.repositoryId = :id")
})
public class Repository {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="repository_id")
    private long repositoryId;

    @Column(length = 128)
    private String url;

    @Temporal(TemporalType.DATE)
    private Date date = new Date();

    public Repository() {
    }

    public Repository(String url, Date date) {
        this.url = url;
        this.date = date;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}