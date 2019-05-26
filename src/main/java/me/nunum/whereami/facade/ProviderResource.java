package me.nunum.whereami.facade;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.controller.ProviderController;
import me.nunum.whereami.model.exceptions.EntityAlreadyExists;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.request.NewProviderRequest;

import javax.annotation.security.PermitAll;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.logging.Level;
import java.util.logging.Logger;

@Api("provider")
@Path("provider")
@Singleton
@PermitAll
public class ProviderResource {

    private static final Logger LOGGER = Logger.getLogger(ProviderResource.class.getSimpleName());

    @Context
    SecurityContext securityContext;


    @POST
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response requestToBeAProvider(NewProviderRequest request) {
        try (final ProviderController controller = new ProviderController()) {

            return Response.ok().entity(controller.registerNewProviderRequest(securityContext.getUserPrincipal(), request).dtoValues()).build();

        } catch (EntityAlreadyExists e) {

            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to persist provider", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GET
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response confirmEmail(@QueryParam("token") String token) {
        try (final ProviderController controller = new ProviderController()) {

            return Response.ok().entity(controller.confirmNewProviderRequest(securityContext.getUserPrincipal(), token).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to persist provider", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
