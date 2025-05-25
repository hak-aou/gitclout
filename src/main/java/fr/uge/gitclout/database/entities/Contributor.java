package fr.uge.gitclout.database.entities;

import jakarta.persistence.*;

@Entity(name = "Contributor")
@Table (name = "CONTRIBUTOR")
@NamedQueries({
        @NamedQuery(name = "getContributorsByTagsId",
                    query = "SELECT c FROM Contributor c WHERE c.tag.tagId = :id")
})
public class Contributor {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="contributor_id")
    private long contributorId;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Column(length = 128)
    private String mail;

    @Column(length = 128)
    private String name;

    @Column(name="codes", columnDefinition = "integer default 0")
    private int codesLines;

    @Column(name="builds", columnDefinition = "integer default 0")
    private int buildsLines;

    @Column(name="configs", columnDefinition = "integer default 0")
    private int configsLines;

    @Column(name="resources", columnDefinition = "integer default 0")
    private int resourcesLines;

    @Column(name="docs", columnDefinition = "integer default 0")
    private int docsLines;

    public Contributor() {
    }

    public Contributor(String mail, String name) {
        this.mail = mail;
        this.name = name;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    public void setMail(String mail) { this.mail = mail; }
    public String getMail() {
        return mail;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }


    public void setCodesLines(int codesLines) {
        this.codesLines = codesLines;
    }
    public int getCodesLines() {
        return codesLines;
    }

    public void setBuildsLines(int buildsLines) {
        this.buildsLines = buildsLines;
    }

    public int getBuildsLines() {
        return buildsLines;
    }

    public void setConfigsLines(int configsLines) {
        this.configsLines = configsLines;
    }

    public int getConfigsLines() {
        return configsLines;
    }

    public void setResourcesLines(int resourcesLines) {
        this.resourcesLines = resourcesLines;
    }

    public int getResourcesLines() {
        return resourcesLines;
    }

    public void setDocsLines(int docsLines) {
        this.docsLines = docsLines;
    }

    public int getDocsLines() {
        return docsLines;
    }

}