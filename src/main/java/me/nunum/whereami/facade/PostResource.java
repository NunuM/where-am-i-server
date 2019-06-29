package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.controller.PostController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.request.PostRequest;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Api("post")
@Path("post")
@Singleton
@PermitAll
public class PostResource {

    private static final Logger LOGGER = Logger.getLogger("PostResource");

    @Context
    SecurityContext securityContext;


    @GET
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response posts(@QueryParam("page") Integer page) {

        try (final PostController controller = new PostController()) {

            final List<DTO> dtoList = controller.posts(Optional.ofNullable(page));

            return Response.ok(dtoList.stream().map(DTO::dtoValues).collect(Collectors.toList()))
                    .expires(new Date(System.currentTimeMillis() + 3600000))
                    .header("Date", new Date())
                    .build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve posts", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @POST
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response newPost(@Valid PostRequest postRequest) {

        try (final PostController controller = new PostController()) {

            return Response.ok(controller.addNewPost(postRequest).dtoValues()).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to save new post", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }


    @PUT
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Path("{id}")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updatePost(@PathParam("id") Long id, PostRequest postRequest) {

        try (final PostController controller = new PostController()) {

            return Response.ok(controller.updatePost(id, postRequest).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to update post", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Something went wrong", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        }
    }

    @DELETE
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Path("{id}")
    @RolesAllowed({"admin"})
    @Produces({MediaType.APPLICATION_JSON})
    public Response deletePost(@PathParam("id") Long id) {

        try (final PostController controller = new PostController()) {

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
