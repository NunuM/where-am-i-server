package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.framework.response.TheMediaType;
import me.nunum.whereami.model.persistance.jpa.*;
import me.nunum.whereami.utils.AppConfig;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import org.glassfish.jersey.server.monitoring.TimeWindowStatistics;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

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


    @GET
    @Path("application")
    @Produces({TheMediaType.APPLICATION_JSON})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-APP", value = "App Instance", required = true, dataType = "string", paramType = "header")
    })
    public Response obtainApplicationStats() {
        final HashMap<String,String> map = new HashMap<>(2);
        final LocalizationRepositoryJpa localizations = new LocalizationRepositoryJpa();
        final DeviceRepositoryJpa devices = new DeviceRepositoryJpa();
        final AlgorithmProviderRepositoryJpa algorithmProviders = new AlgorithmProviderRepositoryJpa();
        final ProviderRepositoryJpa providers = new ProviderRepositoryJpa();

        final Map<String, String> stringStringMap = new JpaRepository<Void, Long>() {
            @Override
            protected String persistenceUnitName() {
                return AppConfig.JPA_UNIT;
            }

            Map<String, String> applicationsStats() {

                final List<String> stringList = Arrays.asList("algorithm", "localization", "device", "provider", "algorithmProvider");

                final String reducedSql = stringList
                        .stream()
                        .reduce("", (acc, c) -> acc + " SELECT '" + c + "' as name, COUNT(*) as cnt FROM " + c + " UNION");

                final String sql = reducedSql.substring(0, reducedSql.length() - 5);

                final String top = "SELECT m.name,\n" +
                        "       p.id,\n" +
                        "       (successPredictions - failurePredictions) / CAST( a.SUCCESSPREDICTIONS  as FLOAT) * 100  as mean\n" +
                        "FROM provider p\n" +
                        "       INNER JOIN algorithmProvider a ON a.provider_id = p.id\n" +
                        "       INNER JOIN algorithm m ON m.alg_id = a.alg_owner_id\n" +
                        "ORDER BY (successPredictions - failurePredictions) desc;";

                final HashMap<String, String> map = new HashMap<>(2);

                final EntityManager entityManager = entityManager();

                final Query nativeQuery = entityManager.createNativeQuery(sql);

                final List<Object[]> topList = entityManager.createNativeQuery(top)
                        .setMaxResults(5)
                        .getResultList();

                final List<Object[]> resultList = nativeQuery.getResultList();

                return null;
            }

        }.applicationsStats();

        map.put("localizations", String.valueOf(localizations.size()));
        map.put("devices", String.valueOf(devices.size()));
        map.put("algorithms", String.valueOf(algorithmProviders.size()));
        map.put("providers", String.valueOf(providers.size()));


        return Response.ok(map).build();
    }

}
