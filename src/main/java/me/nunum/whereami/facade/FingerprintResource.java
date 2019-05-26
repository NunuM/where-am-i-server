package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.controller.FingerprintController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.request.FingerprintRequest;

import javax.annotation.security.PermitAll;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Api(value = "fingerprint")
@Path("fingerprint")
@Singleton
@PermitAll
public class FingerprintResource {

    private static final Logger LOGGER = Logger.getLogger("FingerprintResource");

    @POST
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response collectFingerprint(List<FingerprintRequest> fingerprints) {

        try (final FingerprintController controller = new FingerprintController()) {

            final DTO dto = controller.storeFingerprints(fingerprints);

            return Response.ok(dto.dtoValues()).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to store fingerprints", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        }
    }

}
