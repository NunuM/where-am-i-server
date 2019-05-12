package me.nunum.whereami.facade;

import me.nunum.whereami.controller.FingerprintController;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.request.FingerprintRequest;

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

@Path("fingerprint")
@Singleton
public final class FingerprintResource {

    private static final Logger LOGGER = Logger.getLogger("FingerprintResource");

    private final FingerprintController controller = new FingerprintController();

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response collectFingerprint(List<FingerprintRequest> fingerprints) {

        try {

            final DTO dto = controller.storeFingerprints(fingerprints);

            return Response.ok(dto.dtoValues()).build();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Unable to store fingerprints", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        }
    }

}
