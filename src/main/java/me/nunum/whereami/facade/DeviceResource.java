package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.controller.DeviceController;
import me.nunum.whereami.framework.response.TheMediaType;
import me.nunum.whereami.model.request.UpdateDeviceRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({TheMediaType.APPLICATION_JSON})
    public Response updateDevice(UpdateDeviceRequest request) {

        final DeviceController controller = new DeviceController();

        return Response.ok(controller.updateDevice(securityContext.getUserPrincipal(), request).dtoValues()).build();
    }

}
