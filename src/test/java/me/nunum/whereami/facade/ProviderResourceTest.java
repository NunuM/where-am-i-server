package me.nunum.whereami.facade;

import me.nunum.whereami.framework.interceptor.PrincipalInterceptor;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Provider;
import me.nunum.whereami.model.Role;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.ProviderRepository;
import me.nunum.whereami.model.persistance.RoleRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.ProviderRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.RoleRepositoryJpa;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProviderResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(ProviderResource.class).register(PrincipalInterceptor.class);
    }

    @Test
    public void requestToBeAProvider() {

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("email", "requestToBeAProvider@nunum.me");

        // Create a new provider
        Response response = target("provider")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "requestToBeAProvider")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Insert a valid provider", 200, response.getStatus());

        // Duplicate provider, we send another email
        Response response1 = target("provider")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "requestToBeAProvider")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Insert a valid provider", 200, response1.getStatus());

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "requestToBeAProvider1");

        ProviderRepository providerRepository = new ProviderRepositoryJpa();
        providerRepository.save(new Provider("requestToBeAProvider1@nunum.me", UUID.randomUUID().toString(), true, device));

        try {
            RoleRepository repository = new RoleRepositoryJpa();
            Role role = repository.save(new Role("provider"));
            role.addDevice(device);
            repository.save(role);
        } catch (Exception e) {
            //Ignore
        }

        HashMap<String, Object> otherPayload = new HashMap<>();
        otherPayload.put("email", "requestToBeAProvider@nunum.me");

        // Create a new provider
        Response response2 = target("provider")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "requestToBeAProvider1")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Insert a valid provider", 200, response2.getStatus());

    }

    @Test
    public void confirmEmail() {

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("email", "confirmEmail@nunum.me");

        // Create a new provider
        Response response = target("provider")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "confirmEmail")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Insert a valid provider", 200, response.getStatus());

        HashMap entity = response.readEntity(HashMap.class);
        assertTrue("Must have a valid id", entity.containsKey("id"));

        ProviderRepository providerRepository = new ProviderRepositoryJpa();
        Provider provider = providerRepository.findById(Long.valueOf(entity.get("id").toString())).get();

        try {
            RoleRepository repository = new RoleRepositoryJpa();
            Role role = repository.save(new Role("provider"));
            repository.save(role);
        } catch (Exception e) {
            //Ignore
        }


        // Validate Email
        Response response1 = target("provider")
                .queryParam("token", provider.getToken())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "confirmEmail")
                .buildGet()
                .invoke();

        assertEquals("Confirm email", 200, response1.getStatus());

    }
}