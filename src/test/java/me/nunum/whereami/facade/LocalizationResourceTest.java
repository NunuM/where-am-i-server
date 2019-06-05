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
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocalizationResourceTest extends JerseyTest {


    @Override
    protected Application configure() {
        return new ResourceConfig(LocalizationResource.class).register(PrincipalInterceptor.class);
    }


    @Test
    public void retrieveLocalizations() {

        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "retrieveLocalizations");
        Device device1 = deviceRepository.findOrPersist(() -> "notRetrieveLocalizations");


        for (int i = 0; i < 30; i++) {
            localizationRepository.save(new Localization("retrieveLocalizations" + i,
                    "retrieveLocalizations",
                    0.0,
                    0.0,
                    i % 2 == 0,
                    device));
        }

        // Get first page for the localization owner
        Vector retrieveLocalizations = target("localization")
                .queryParam("page", "-1")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "retrieveLocalizations")
                .buildGet()
                .invoke(Vector.class);

        assertEquals("First page must retrieve 20 records", 20, retrieveLocalizations.size());


        // Get second page for the localization owner
        Vector retrieveLocalizations1 = target("localization")
                .queryParam("page", "2")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "retrieveLocalizations")
                .buildGet()
                .invoke(Vector.class);

        assertTrue("Seconds page must retrieve the reaming ", 10 <= retrieveLocalizations1.size());


        // Get first page for random user that is not localization owner of any entity
        Vector retrieveLocalizations2 = target("localization")
                .queryParam("page", "1")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "notRetrieveLocalizations")
                .buildGet()
                .invoke(Vector.class);

        assertTrue("Expecting to receive half of the 30 localization inserted due the public state", 15 <= retrieveLocalizations2.size());


        // Get first page for the localization owner for a given name
        Vector retrieveLocalizations3 = target("localization")
                .queryParam("page", "1")
                .queryParam("name", "retrieveLocalizations1")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "retrieveLocalizations")
                .buildGet()
                .invoke(Vector.class);

        assertEquals("Expecting one raw that is private but the requester is the owner", 1, retrieveLocalizations3.size());


        // Try to search by name for a not owner localization
        Vector retrieveLocalizations4 = target("localization")
                .queryParam("page", "1")
                .queryParam("name", "retrieveLocalizations1")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "notRetrieveLocalizations")
                .buildGet()
                .invoke(Vector.class);

        assertEquals("Expecting 0 raw that is private and the requester is not the owner", 0, retrieveLocalizations4.size());


        // Save one localization with same name but different owner
        localizationRepository.save(new Localization("retrieveLocalizations1",
                "notRetrieveLocalizations",
                0.0,
                0.0,
                false,
                device1));


        // Try to search by name for a localization owner
        Vector retrieveLocalizations5 = target("localization")
                .queryParam("page", "1")
                .queryParam("name", "retrieveLocalizations1")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "notRetrieveLocalizations")
                .buildGet()
                .invoke(Vector.class);

        assertEquals("Expecting 1 raw", 1, retrieveLocalizations5.size());

    }

    @Test
    public void newLocalization() {

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("label", "newLocalization");
        payload.put("isPublic", false);
        payload.put("user", "newLocalization");
        payload.put("latitude", 0.0);
        payload.put("longitude", 0.0);

        Response localization = target("localization")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "newLocalization")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Insert a valid localization", 200, localization.getStatus());

        Response response = target("localization")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "newLocalization")
                .buildPost(Entity.json(payload))
                .invoke();
        assertEquals("Duplicate localization", 409, response.getStatus());

    }

    @Test
    public void deleteLocalization() {

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("label", "deleteLocalization");
        payload.put("isPublic", false);
        payload.put("user", "deleteLocalization");
        payload.put("latitude", 0.0);
        payload.put("longitude", 0.0);

        Response localization = target("localization")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deleteLocalization")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Insert a valid localization", 200, localization.getStatus());

        HashMap entity = localization.readEntity(HashMap.class);
        assertTrue("Localization id", entity.containsKey("id"));

        // Delete non existing localization
        Response response = target("localization/-1")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deleteLocalization")
                .buildDelete()
                .invoke();
        assertEquals("Localization not found", 404, response.getStatus());


        // Not owner trying to delete localization
        Response response1 = target("localization/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "notDeleteLocalization")
                .buildDelete()
                .invoke();

        assertEquals("Localization cannot be delete due ownership", 403, response1.getStatus());


        // Owner localization deletion
        Response response2 = target("localization/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deleteLocalization")
                .buildDelete()
                .invoke();
        assertEquals("Localization deleted", 200, response2.getStatus());


        DeviceRepositoryJpa deviceRepository = new DeviceRepositoryJpa();
        final List<Device> deviceList = deviceRepository.page(1, 200);

        final Optional<Device> device = deviceList.stream().filter(e -> e.instanceId().equalsIgnoreCase("deleteLocalization")).findFirst();

        assertTrue("Must remain the device", device.isPresent());


    }
}