package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.controller.LocalizationController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.response.TheMediaType;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.exceptions.EntityAlreadyExists;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

@Api("spam")
@PermitAll
public class LocalizationReportResource {

    private static final Logger LOGGER = Logger.getLogger(LocalizationReportResource.class.getSimpleName());

    private final Principal principal;
    private final Localization localization;
    private final LocalizationController controller;


    public LocalizationReportResource(Principal principal,
                                      Localization localization,
                                      LocalizationController controller) {
        this.principal = principal;
        this.controller = controller;
        this.localization = localization;
    }

    @POST
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({TheMediaType.APPLICATION_JSON})
    public Response localizationSpam() {

        try {
            final DTO reportDto = this.controller.newSpamReport(principal, localization);

            return Response.ok(reportDto.dtoValues()).build();

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

