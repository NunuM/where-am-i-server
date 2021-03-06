package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.controller.FingerprintController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.response.TheMediaType;
import me.nunum.whereami.model.request.FingerprintRequest;

import javax.annotation.security.PermitAll;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Api(value = "fingerprint")
@Path("fingerprint")
@Singleton
@PermitAll
public class FingerprintResource {

    private static final Logger LOGGER = Logger.getLogger("FingerprintResource");

    @Context
    private SecurityContext securityContext;

    @POST
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({TheMediaType.APPLICATION_JSON})
    public Response collectFingerprint(List<FingerprintRequest> fingerprints) {

        try (final FingerprintController controller = new FingerprintController()) {

            List<Map<String, Object>> list = controller.storeFingerprints(securityContext.getUserPrincipal(), fingerprints).stream().map(DTO::dtoValues).collect(Collectors.toList());

            return Response.ok(list).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to store fingerprints", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        }
    }

}
