package fr.uge.gitclout.database.api;

import fr.uge.gitclout.database.entities.Contributor;
import fr.uge.gitclout.database.entities.Repository;
import fr.uge.gitclout.database.entities.Tag;
import fr.uge.gitclout.database.main.GitClone;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.core.Response;
import org.eclipse.jgit.api.errors.GitAPIException;

@Path("/api")
public class EntityResource {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("DataBaseGitClout");

    @Path("/analyze/insertTags")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendDataAndGetTags(Repository data) throws GitAPIException, IOException {
        GitClone.insertTags(data.getUrl());
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/analyze/all")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendData(Repository data) throws GitAPIException, IOException {
        GitClone.analyzeRepository(data.getUrl());
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/analyze/tag")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response analyzeTag(Tag sendTag) throws GitAPIException, IOException {
        Tag tag = getTagById(sendTag.getTagId());
        GitClone.analyzeSpecificTag(tag);
        return Response.status(Response.Status.OK).build();
    }


    @Path("/repositories/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Repository> getRepositoryName() {
        return entityManagerFactory.createEntityManager().createNamedQuery("getAllRepositories", Repository.class).getResultList();
    }


    @Path("/tags/repository/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tag> getTagsByRepositoryId(@Valid @PathParam("id") long id) {
        var tagList = entityManagerFactory.createEntityManager().createNamedQuery("getTagsByRepositoryId", Tag.class)
                .setParameter("id", id)
                .getResultList();
        if (tagList.isEmpty()) {
            throw new NotFoundException("Unable to find the tag with the repository id " + tagList);
        }
        return tagList;
    }

    @Path("/tag/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Tag getTagById(@Valid @PathParam("id") long id) {
        var tag = entityManagerFactory.createEntityManager().createNamedQuery("getTagById", Tag.class)
                .setParameter("id", id)
                .getResultList()
                .getFirst();
        if (tag == null) {
            throw new NotFoundException("Unable to find the tag with the id ");
        }
        return tag;
    }

    @Path("/repository/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Repository getRepositoryById(@Valid @PathParam("id") long id) {
        Repository repository = entityManagerFactory.createEntityManager().createNamedQuery("getRepositoryById", Repository.class)
                .setParameter("id", id)
                .getResultList()
                .getFirst();
        if (repository == null) {
            throw new NotFoundException("Unable to find the repository with the id " + id);
        }
        return repository;
    }

    @Path("/contributors/tag/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contributor> getContributorsByTagsId(@Valid @PathParam("id") long id) {
        var listContributors =  entityManagerFactory.createEntityManager().createNamedQuery("getContributorsByTagsId", Contributor.class)
                .setParameter("id", id)
                .getResultList();

        if (listContributors.isEmpty()) {
            throw new NotFoundException("Unable to find the tag by the id " + listContributors);
        }
        return  listContributors;
    }
}