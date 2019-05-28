package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.controller.AlgorithmController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.dto.ErrorDTO;
import me.nunum.whereami.model.exceptions.EntityAlreadyExists;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityAccessException;
import me.nunum.whereami.model.request.*;

import javax.annotation.security.PermitAll;
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

@Api(value = "algorithm")
@Path("algorithm")
@Singleton
@PermitAll
public class AlgorithmResource {

    private static final Logger LOGGER = Logger.getLogger(AlgorithmResource.class.getSimpleName());

    @Context
    SecurityContext securityContext;

    @GET
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Produces({MediaType.APPLICATION_JSON})
    public Response availableAlgorithm(@QueryParam("page") Integer page) {

        try (final AlgorithmController controller = new AlgorithmController()) {

            final List<DTO> dtoList = controller.algorithms(Optional.ofNullable(page));

            return Response.ok(dtoList.stream().map(DTO::dtoValues).collect(Collectors.toList())).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithms", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Path("{it}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAlgorithm(@PathParam("it") Long aId) {

        try (final AlgorithmController controller = new AlgorithmController()) {

            return Response.ok(controller.algorithm(aId).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithm", e);

            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithms", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response newAlgorithm(@Valid NewAlgorithmRequest algorithmRequest) {

        try (final AlgorithmController controller = new AlgorithmController()) {

            return Response.ok(controller.addNewAlgorithm(securityContext.getUserPrincipal(), algorithmRequest).dtoValues()).build();

        } catch (EntityAlreadyExists e) {

            LOGGER.log(Level.SEVERE, "Entity already exists", e);

            return Response.status(Response.Status.CONFLICT).entity(ErrorDTO.fromError(e)).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithms", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Path("{it}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateAlgorithm(@PathParam("it") Long aId, UpdateAlgorithmRequest request) {
        try (final AlgorithmController controller = new AlgorithmController()) {

            return Response.ok().entity(controller.updateAlgorithm(securityContext.getUserPrincipal(), aId, request).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to find algorithm", e);

            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (ForbiddenEntityAccessException e) {

            return Response.status(Response.Status.FORBIDDEN).build();

        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DELETE
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Path("{it}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("admin")
    public Response deleteAlgorithm(@PathParam("it") Long aId) {

        try (final AlgorithmController controller = new AlgorithmController()) {

            return Response.ok().entity(controller.deleteAlgorithm(aId).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to find algorithm", e);

            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PUT
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Path("{it}/approval")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("admin")
    public Response approveAlgorithm(@PathParam("it") Long aId, ApprovalRequest request) {

        try (final AlgorithmController controller = new AlgorithmController()) {

            return Response.ok().entity(controller.updateEntityApproval(aId, request).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to find algorithm", e);

            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DELETE
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Path("{it}/provider/{pr}")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"provider"})
    public Response deleteAlgorithmProvider(@PathParam("it") Long aId, @PathParam("pr") Long pId) {

        try (final AlgorithmController controller = new AlgorithmController()) {

            return Response.status(Response.Status.OK).entity(controller.deleteProvider(aId, pId, securityContext.getUserPrincipal()).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve provider", e);

            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (ForbiddenEntityAccessException e) {

            return Response.status(Response.Status.FORBIDDEN).build();

        } catch (IllegalStateException e) {

            return Response.status(Response.Status.BAD_REQUEST).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Something bad on delete provider", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @POST
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Path("{it}/provider")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"provider"})
    public Response addAlgorithmProvider(@PathParam("it") Long aId, NewAlgorithmProvider algorithmProvider) {

        try (final AlgorithmController controller = new AlgorithmController()) {

            return Response.ok(controller.registerNewAlgorithmProvider(aId, algorithmProvider, securityContext.getUserPrincipal()).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithm", e);

            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (IllegalArgumentException e) {

            LOGGER.log(Level.SEVERE, "Invalid request", e);

            return Response.status(Response.Status.BAD_REQUEST).entity(ErrorDTO.fromError(e)).build();

        } catch (EntityAlreadyExists e) {

            LOGGER.log(Level.SEVERE, "Entity already exists", e);

            return Response.status(Response.Status.CONFLICT).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithms", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PUT
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Path("{it}/provider/{pr}")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"provider"})
    public Response updateProvider(@PathParam("it") Long aId, @PathParam("pr") Long pId, UpdateAlgorithmProvider request) {

        try (final AlgorithmController controller = new AlgorithmController()) {

            return Response.ok().entity(controller.updateProvider(securityContext.getUserPrincipal(), aId, pId, request).dtoValues()).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithm", e);

            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (IllegalArgumentException e) {

            LOGGER.log(Level.SEVERE, "Invalid request", e);

            return Response.status(Response.Status.BAD_REQUEST).entity(ErrorDTO.fromError(e)).build();

        } catch (EntityAlreadyExists e) {

            LOGGER.log(Level.SEVERE, "Entity already exists", e);

            return Response.status(Response.Status.CONFLICT).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithms", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
