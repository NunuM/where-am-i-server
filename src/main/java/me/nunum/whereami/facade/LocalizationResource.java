package me.nunum.whereami.facade;

import me.nunum.whereami.controller.LocalizationController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.exceptions.EntityAlreadyExists;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityDeletionException;
import me.nunum.whereami.model.request.NewLocalizationRequest;

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

@Path("localization")
@Singleton
public final class LocalizationResource {

    @Context
    SecurityContext securityContext;

    private static final Logger LOGGER = Logger.getLogger("LocalizationResource");

    private final LocalizationController controller = new LocalizationController();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response retrieveLocalizations(@QueryParam("page") Integer page, @QueryParam("name") String localizationName) {
        try {
            final List<DTO> dtos = controller.localizations(securityContext.getUserPrincipal(),
                    Optional.ofNullable(page),
                    Optional.ofNullable(localizationName)
            );

            return Response.ok(dtos.stream().map(DTO::dtoValues).collect(Collectors.toList())).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to response with localizations", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response newLocalization(@Valid NewLocalizationRequest localizationRequest) {
        try {

            final DTO localization = this.controller.newLocalization(securityContext.getUserPrincipal(), localizationRequest);

            return Response.ok(localization.dtoValues()).build();

        } catch (EntityAlreadyExists e) {

            LOGGER.log(Level.SEVERE, "Localization already exists", e);

            return Response.status(Response.Status.CONFLICT).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to create new localization", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteLocalization(@PathParam("id") Long id) {
        try {
            final DTO localizationDto = this.controller.deleteLocalizationRequest(securityContext.getUserPrincipal(), id);

            return Response.ok(localizationDto.dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Localization not found", e);

            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ForbiddenEntityDeletionException e) {

            LOGGER.log(Level.SEVERE, "Localization not belongs to requester", e);

            return Response.status(Response.Status.FORBIDDEN).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to create new localization", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Path("spam")
    public LocalizationReportResource reportResource() {
        return new LocalizationReportResource(controller, securityContext);
    }

    @Path("{id}/train")
    public Object trainResource(@PathParam("id") Long id) {
        try {
            return new TrainResource(this.controller.localization(id), securityContext);
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("{id}/position")
    public Object positionResource(@PathParam("id") Long id) {
        try {
            return new PositionResource(this.controller.localization(id), securityContext);
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
