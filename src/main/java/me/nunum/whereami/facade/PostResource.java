package me.nunum.whereami.facade;

import me.nunum.whereami.controller.PostController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.request.PostRequest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("post")
@Singleton
public final class PostResource {

    private static final Logger LOGGER = Logger.getLogger("PostResource");

    private final PostController controller = new PostController();

    @Context
    SecurityContext securityContext;


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response posts(@QueryParam("page") Integer page) {

        try {

            final List<DTO> dtoList = controller.posts(Optional.ofNullable(page));

            return Response.ok(dtoList.stream().map(DTO::dtoValues).collect(Collectors.toList())).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve posts", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @POST
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response newPost(@Valid PostRequest postRequest) {

        try {

            return Response.ok(this.controller.addNewPost(postRequest).dtoValues()).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to save new post", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }


    @PUT
    @Path("{id}")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updatePost(@PathParam("id") Long id, PostRequest postRequest) {

        try {

            return Response.ok(this.controller.updatePost(id, postRequest).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to update post", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Something went wrong", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        }
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"admin"})
    @Produces({MediaType.APPLICATION_JSON})
    public Response deletePost(@PathParam("id") Long id) {

        try {

            return Response.ok(controller.deletePost(id).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to delete post", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Something bad", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        }
    }
}
