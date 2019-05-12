package me.nunum.whereami.facade;

import me.nunum.whereami.controller.AlgorithmController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
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

@Path("algorithm")
@Singleton
public final class AlgorithmResource {

    private static final Logger LOGGER = Logger.getLogger("AlgorithmResource");

    private final AlgorithmController controller = new AlgorithmController();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response availableAlgorithm(@QueryParam("page") Integer page) {

        try {

            final List<DTO> dtoList = controller.algorithms(Optional.ofNullable(page));

            return Response.ok(dtoList.stream().map(DTO::dtoValues).collect(Collectors.toList())).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithms", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{it}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAlgorithm(@PathParam("it") Long aId) {
        try {

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
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response newAlgorithm(@Valid NewAlgorithmRequest algorithmRequest) {

        try {

            return Response.ok(this.controller.addNewAlgorithm(algorithmRequest).dtoValues()).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to retrieve algorithms", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
