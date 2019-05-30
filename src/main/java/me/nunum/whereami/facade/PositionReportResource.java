package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import me.nunum.whereami.controller.PositionsController;
import me.nunum.whereami.model.exceptions.EntityAlreadyExists;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.request.PositionSpamRequest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.logging.Level;
import java.util.logging.Logger;


@Api("spam")
@PermitAll
public class PositionReportResource {

    private static final Logger LOGGER = Logger.getLogger(PositionReportResource.class.getSimpleName());

    private final PositionsController controller;
    private final SecurityContext securityContext;

    public PositionReportResource(PositionsController controller,
                                  SecurityContext securityContext) {
        this.controller = controller;
        this.securityContext = securityContext;
    }

    @POST
    public Response positionSpam(PositionSpamRequest request) {

        try {
            return Response
                    .ok(controller.processSpamRequest(securityContext.getUserPrincipal(), request))
                    .build();
        } catch (EntityAlreadyExists e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } finally {
            try {
                this.controller.close();
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "Could not close entity manager", exception);
            }
        }

    }
}
