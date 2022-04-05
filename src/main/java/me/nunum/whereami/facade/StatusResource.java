package me.nunum.whereami.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.framework.response.TheMediaType;
import me.nunum.whereami.utils.AppConfig;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import org.glassfish.jersey.server.monitoring.TimeWindowStatistics;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        final Map<String, Object> stringStringMap = new JpaRepository<Void, Long>() {


            @Override
            protected String persistenceUnitName() {
                return AppConfig.JPA_UNIT;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> applicationsStats() {

                final List<String> stringList = Arrays.asList("algorithm", "localization", "device", "provider", "algorithmProvider");

                final String reducedSql = stringList
                        .stream()
                        .reduce("", (acc, c) -> acc + " SELECT '" + c + "' AS name, COUNT(*) AS cnt FROM " + c + " UNION");

                final String sql = reducedSql.substring(0, reducedSql.length() - 5);

                final String top = "SELECT m.name, p.id, ((successPredictions) / NULLIF(CAST( (a.SUCCESSPREDICTIONS + a.failurePredictions) AS FLOAT),0)) * 100  AS top FROM provider p INNER JOIN algorithmProvider a ON a.provider_id = p.id INNER JOIN algorithm m ON m.alg_id = a.alg_owner_id ORDER BY 3 DESC ;";

                final HashMap<String, Object> map = new HashMap<>(6);

                final EntityManager entityManager = entityManager();

                final Query entitiesStatus = entityManager.createNativeQuery(sql);

                final List<Object[]> topList = entityManager.createNativeQuery(top)
                        .setMaxResults(5)
                        .getResultList();

                final List<Object[]> resultList = entitiesStatus.getResultList();

                resultList.forEach(e -> map.put(e[0].toString(), e[1]));

                final List<HashMap<String, Object>> jsonObjects = topList.stream().map(e -> {
                    final HashMap<String, Object> hashMap = new HashMap<>(3);
                    hashMap.put("name", e[0].toString());
                    hashMap.put("providerId", Long.valueOf(e[1].toString()));

                    if (e[2] != null) {
                        hashMap.put("rate", Float.valueOf(e[2].toString()));
                    } else {
                        hashMap.put("rate", 0.0f);
                    }

                    return hashMap;
                }).collect(Collectors.toList());

                map.put("top", jsonObjects);

                return map;
            }

        }.applicationsStats();


        return Response.ok(stringStringMap).build();
    }

}
