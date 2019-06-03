package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import me.nunum.whereami.controller.DeviceController;
import me.nunum.whereami.model.request.UpdateDeviceRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Api("device")
@Path("device")
public class DeviceResource {

    @Context
    private SecurityContext securityContext;


    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateDevice(UpdateDeviceRequest request) {

        final DeviceController controller = new DeviceController();

        return Response.ok(controller.updateDevice(securityContext.getUserPrincipal(), request).dtoValues()).build();
    }

}
