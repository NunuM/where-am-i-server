package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import me.nunum.whereami.controller.PositionsController;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.exceptions.EntityAlreadyExists;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;

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

    private final Position position;
    private final PositionsController controller;
    private final SecurityContext securityContext;

    public PositionReportResource(Position position,
                                  PositionsController controller,
                                  SecurityContext securityContext) {
        this.position = position;
        this.controller = controller;
        this.securityContext = securityContext;
    }

    @POST
    public Response positionSpam() {

        try {
            return Response
                    .ok(controller.processSpamRequest(securityContext.getUserPrincipal(), this.position))
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
