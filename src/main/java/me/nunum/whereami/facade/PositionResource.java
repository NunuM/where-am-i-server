package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.controller.PositionsController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityDeletionException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityModificationException;
import me.nunum.whereami.model.request.NewPositionRequest;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Api(value = "position")
@PermitAll
public class PositionResource {

    private static final Logger LOGGER = Logger.getLogger("PositionResource");

    private final Localization localization;
    private final SecurityContext securityContext;
    private final PositionsController controller;

    public PositionResource(Localization localization,
                            SecurityContext securityContext) {

        this.localization = localization;
        this.securityContext = securityContext;
        this.controller = new PositionsController(localization);
    }


    @GET
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response positions() {

        try {

            final List<DTO> dtos = this.controller.positions();

            return Response.ok(dtos.stream().map(DTO::dtoValues).collect(Collectors.toList())).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve positions from localization " + this.localization.id(), e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @POST
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response newPosition(@Valid NewPositionRequest newPositionRequest) {

        try {

            final DTO dto = this.controller.newPosition(securityContext, newPositionRequest);

            return Response.ok(dto.dtoValues()).build();

        } catch (ForbiddenEntityModificationException e) {

            LOGGER.log(Level.SEVERE, "Localization not belongs to requester", e);

            return Response.status(Response.Status.FORBIDDEN).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Something went wrong", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            try {
                this.controller.close();
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "Could not close entity manager", exception);
            }
        }
    }


    @DELETE
    @Path("{ip}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response deletePosition(@PathParam("ip") Long ip) {
        try {

            final DTO dto = this.controller.deletePosition(securityContext, ip);

            return Response.ok(dto.dtoValues()).build();

        } catch (ForbiddenEntityDeletionException e) {

            LOGGER.log(Level.SEVERE, "Localization not belongs to requester", e);

            return Response.status(Response.Status.FORBIDDEN).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Localization position not found", e);

            return Response.status(Response.Status.FORBIDDEN).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Something went wrong", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            try {
                this.controller.close();
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "Could not close entity manager", exception);
            }
        }
    }


    @Path("{ip}/spam")
    public PositionReportResource positionReportResource() {
        return new PositionReportResource(controller, securityContext);
    }
}
