package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.controller.FeedbackController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.response.TheMediaType;
import me.nunum.whereami.model.request.NewFeedbackRequest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Api("feedback")
@Path("feedback")
public class FeedbackResource {

    private static final Logger LOGGER = Logger.getLogger(FeedbackResource.class.getSimpleName());

    @Context
    private SecurityContext securityContext;


    @GET
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({TheMediaType.APPLICATION_JSON})
    @RolesAllowed("admin")
    public Response listFeedback() {
        try (final FeedbackController controller = new FeedbackController()) {

            final List<DTO> dtos = controller.listAllFeedback();

            return Response.ok(dtos.stream().map(DTO::dtoValues).collect(Collectors.toList())).build();

        } catch (Exception e) {

            LOGGER.log(Level.INFO, "Could not retrieve feedback", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({TheMediaType.APPLICATION_JSON})
    public Response submitFeedback(NewFeedbackRequest request) {

        try (final FeedbackController controller = new FeedbackController()) {

            return Response.ok(controller.processFeedback(securityContext.getUserPrincipal(), request).dtoValues()).build();

        } catch (Exception e) {

            LOGGER.log(Level.INFO, "Could not store feedback", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
