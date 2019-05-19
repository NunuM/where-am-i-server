package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import me.nunum.whereami.controller.PositionsController;
import me.nunum.whereami.model.exceptions.EntityAlreadyExists;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.request.PostionSpamRequest;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


@Api("spam")
public class PositionReportResource {


    private final PositionsController positionsController;
    private final SecurityContext securityContext;

    public PositionReportResource(PositionsController positionsController,
                                  SecurityContext securityContext) {
        this.positionsController = positionsController;
        this.securityContext = securityContext;
    }

    @POST
    public Response positionSpam(PostionSpamRequest request) {

        try {
            return Response
                    .ok(positionsController.processSpamRequest(securityContext.getUserPrincipal(), request))
                    .build();
        } catch (EntityAlreadyExists e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

}
