package me.nunum.whereami.facade;

import me.nunum.whereami.framework.interceptor.PrincipalInterceptor;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.LocalizationRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.LocalizationRepositoryJpa;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PositionReportResourceTest extends JerseyTest {


    @Override
    protected Application configure() {
        return new ResourceConfig(LocalizationResource.class, PositionResource.class, PositionReportResource.class).register(PrincipalInterceptor.class);
    }


    @Test
    public void positionSpam() {
        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "positionSpam");

        final Localization localization = localizationRepository.save(new Localization("positionSpam",
                "newPosition",
                0.0,
                0.0,
                false,
                device));


        HashMap<String, Object> payload = new HashMap<>();
        payload.put("label", "newPosition");

        // Create new valid position
        final Response response = target("localization/" + localization.id() + "/position")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "positionSpam")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("New position", 200, response.getStatus());
        HashMap<String, Object> entity = response.readEntity(HashMap.class);
        assertTrue("Valid id", entity.containsKey("id"));


        HashMap<String, Object> report = new HashMap<>();
        report.put("id", entity.get("id").toString());
        report.put("className", "Position");

        // Create new report
        final Response response1 = target("localization/" + localization.id() + "/position/" + entity.get("id").toString() + "/spam")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "positionSpam")
                .buildPost(Entity.json(report))
                .invoke();
        assertEquals("New position report", 200, response1.getStatus());


        // Create duplicate report
        final Response response2 = target("localization/" + localization.id() + "/position/" + entity.get("id").toString() + "/spam")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "positionSpam")
                .buildPost(Entity.json(report))
                .invoke();
        assertEquals("New position report", 409, response2.getStatus());


        // Create duplicate report
        report.put("id", -333);
        final Response response3 = target("localization/" + localization.id() + "/position/-333/spam")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "positionSpam")
                .buildPost(Entity.json(report))
                .invoke();
        assertEquals("New position report", 404, response3.getStatus());


    }
}