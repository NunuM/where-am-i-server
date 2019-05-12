package me.nunum.whereami.facade;

import me.nunum.whereami.controller.TrainingController;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityAccessException;
import me.nunum.whereami.model.request.NewTrainingRequest;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class TrainResource {

    private static final Logger LOGGER = Logger.getLogger("TrainResource");

    private final Localization localization;
    private final SecurityContext securityContext;
    private final TrainingController controller;

    public TrainResource(Localization localization, SecurityContext securityContext) {
        this.localization = localization;
        this.securityContext = securityContext;
        this.controller = new TrainingController();
    }


    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response submitTrainingRequest(@Valid NewTrainingRequest request) {
        try {
            return Response.ok(controller
                    .submitTrainingRequest(securityContext.getUserPrincipal(), request, localization)
                    .dtoValues()
            ).build();
        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to find entity", e);

            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("{it}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response trainingStatus(@PathParam("it") Long it) {

        try {

            return Response.ok(controller.
                    trainingStatus(securityContext.getUserPrincipal(), it)
                    .dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to find entity", e);

            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (ForbiddenEntityAccessException e) {

            LOGGER.log(Level.SEVERE, "Unable to find entity", e);

            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response allTrainingStatus(@HeaderParam("timezone") String timezone) {

        try {

            return Response.ok(controller.
                    allTrainingStatus(securityContext.getUserPrincipal(), localization)
                    .stream()
                    .map(e -> e.dtoValues(timezone))
                    .collect(Collectors.toList())).build();


        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to find entity", e);

            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (ForbiddenEntityAccessException e) {

            LOGGER.log(Level.SEVERE, "Unable to find entity", e);

            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

}
