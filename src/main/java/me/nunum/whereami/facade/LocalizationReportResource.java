package me.nunum.whereami.facade;

import me.nunum.whereami.controller.LocalizationController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.request.LocalizationSpamRequest;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


public final class LocalizationReportResource {

    private final LocalizationController controller;
    private final SecurityContext securityContext;

    public LocalizationReportResource(LocalizationController controller,
                                      SecurityContext securityContext) {
        this.controller = controller;
        this.securityContext = securityContext;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response localizationSpam(@Valid LocalizationSpamRequest spamRequest) {
        try {

            final DTO reportDto = this.controller.newSpamReport(securityContext.getUserPrincipal(), spamRequest);

            return Response.ok(reportDto.dtoValues()).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
