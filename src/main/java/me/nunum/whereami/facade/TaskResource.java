package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.controller.TaskController;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityAccessException;
import me.nunum.whereami.model.request.UpdateTask;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.logging.Level;
import java.util.logging.Logger;

@Api("task")
@Path("task")
@Singleton
@PermitAll
public class TaskResource {

    private final static Logger LOGGER = Logger.getLogger(TaskResource.class.getSimpleName());

    @Context
    private SecurityContext securityContext;

    @PUT
    @Path("{taskId}")
    @RolesAllowed("provider")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    public Response updateTask(@PathParam("taskId") Long taskId, UpdateTask request) {
        try (final TaskController controller = new TaskController()) {

            return Response.ok(controller.updateTask(securityContext.getUserPrincipal(), taskId, request).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.INFO, "Entity {0} not found", taskId);

            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ForbiddenEntityAccessException e) {

            LOGGER.log(Level.INFO, "Forbidden access on task {0}", taskId);

            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Error while update task", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
