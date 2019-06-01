package me.nunum.whereami.facade;

import me.nunum.whereami.framework.interceptor.PrincipalInterceptor;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.LocalizationRepository;
import me.nunum.whereami.model.persistance.PositionRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.LocalizationRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.PostitionRepositoryJpa;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PositionResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(LocalizationResource.class, PositionResource.class).register(PrincipalInterceptor.class);
    }

    @Test
    public void positions() {

        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "positions");

        final Localization localization = localizationRepository.save(new Localization("positions",
                "positions",
                0.0,
                0.0,
                false,
                device));

        PositionRepository positionRepository = new PostitionRepositoryJpa();

        for (int i = 0; i < 30; i++) {
            positionRepository.save(new Position("positions" + i, localization));
        }

        // List all entities for the owner
        final Response response = target("localization/" + localization.id() + "/position")
                .queryParam("page", -1)
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "positions")
                .buildGet()
                .invoke();

        assertEquals("Valid response", 200, response.getStatus());
        assertEquals("Must see 30 rows", 30, response.readEntity(Vector.class).size());


        // Positions only can be seen by the owner
        final Response response2 = target("localization/" + localization.id() + "/position")
                .queryParam("page", -1)
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "notPositions")
                .buildGet()
                .invoke();
        assertEquals("Forbidden response", 403, response2.getStatus());
    }

    @Test
    public void newPosition() {

        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "newPosition");


        final Localization localization = localizationRepository.save(new Localization("newPosition",
                "newPosition",
                0.0,
                0.0,
                true,
                device));


        HashMap<String, Object> payload = new HashMap<>();
        payload.put("label", "newPosition");

        // Create new valid position
        final Response response = target("localization/" + localization.id() + "/position")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "newPosition")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("New position", 200, response.getStatus());


        // Not allowed to add new position
        final Response response1 = target("localization/" + localization.id() + "/position")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "NotNewPosition")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Forbidden", 403, response1.getStatus());


    }

    @Test
    public void deletePosition() {


        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "deletePosition");


        final Localization localization = localizationRepository.save(new Localization("deletePosition",
                "deletePosition",
                0.0,
                0.0,
                true,
                device));


        HashMap<String, Object> payload = new HashMap<>();
        payload.put("label", "deletePosition");

        // Create new valid position
        final Response response = target("localization/" + localization.id() + "/position")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deletePosition")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("New position", 200, response.getStatus());
        HashMap<String, Object> entity = response.readEntity(HashMap.class);
        assertTrue("Valid id", entity.containsKey("id"));


        // Not a owner cannot delete position
        final Response response1 = target("localization/" + localization.id() + "/position/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "NotDeletePosition")
                .buildDelete()
                .invoke();
        assertEquals("Delete position forbidden", 403, response1.getStatus());


        // Owner can delete position
        final Response response2 = target("localization/" + localization.id() + "/position/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deletePosition")
                .buildDelete()
                .invoke();
        assertEquals("Delete position", 200, response2.getStatus());


        // Delete non existing position
        final Response response3 = target("localization/" + localization.id() + "/position/-1")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deletePosition")
                .buildDelete()
                .invoke();
        assertEquals("Delete non existing position", 404, response3.getStatus());
    }
}