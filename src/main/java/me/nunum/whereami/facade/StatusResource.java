package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import org.glassfish.jersey.server.monitoring.TimeWindowStatistics;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api("status")
@Path("status")
public class StatusResource {


    @Inject
    Provider<MonitoringStatistics> statistics;

    @GET
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    @Produces({MediaType.TEXT_HTML})
    @RolesAllowed({"admin"})
    public String getTotalExceptionMappings() throws InterruptedException {
        final MonitoringStatistics snapshot
                = statistics.get().snapshot();

        final TimeWindowStatistics timeWindowStatistics
                = snapshot.getRequestStatistics()
                .getTimeWindowStatistics().get(0l);

        return "request count: " + timeWindowStatistics.getRequestCount()
                + ", average request processing [ms]: "
                + timeWindowStatistics.getAverageDuration();
    }

}
