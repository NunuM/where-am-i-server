package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.controller.AlgorithmController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.exceptions.EntityAlreadyExists;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.request.NewAlgorithmProvider;
import me.nunum.whereami.model.request.NewAlgorithmRequest;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Api(value = "algorithm")
@Path("algorithm")
@Singleton
public class AlgorithmResource {

    private static final Logger LOGGER = Logger.getLogger("AlgorithmResource");

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
    @Path("{it}/provider")
    @Produces({MediaType.APPLICATION_JSON})
    public Response addAlgorithmProvider(@PathParam("it") Long aId, NewAlgorithmProvider algorithmProvider) {

        try (final AlgorithmController controller = new AlgorithmController()) {

            return Response.ok(controller.registerNewAlgorithmProvider(aId, algorithmProvider)).build();

        } catch (EntityNotFoundException e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithm", e);

            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (IllegalArgumentException e) {

            LOGGER.log(Level.SEVERE, "Invalid request", e);

            return Response.status(Response.Status.BAD_REQUEST).build();

        } catch (EntityAlreadyExists e) {

            LOGGER.log(Level.SEVERE, "Entity already exists", e);

            return Response.status(Response.Status.CONFLICT).build();

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

            return Response.ok(controller.addNewAlgorithm(algorithmRequest).dtoValues()).build();

        } catch (EntityAlreadyExists e) {

            LOGGER.log(Level.SEVERE, "Entity already exists", e);

            return Response.status(Response.Status.CONFLICT).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithms", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
