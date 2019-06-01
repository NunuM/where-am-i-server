package me.nunum.whereami.facade;

import me.nunum.whereami.framework.interceptor.PrincipalInterceptor;
import me.nunum.whereami.model.*;
import me.nunum.whereami.model.persistance.*;
import me.nunum.whereami.model.persistance.jpa.*;
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

public class TrainResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(LocalizationResource.class,
                PositionResource.class,
                TrainResource.class)
                .register(PrincipalInterceptor.class);
    }

    @Test
    public void submitTrainingRequest() {

        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "submitTrainingRequest");


        final Localization localization = localizationRepository.save(new Localization("submitTrainingRequest",
                "submitTrainingRequest",
                0.0,
                0.0,
                false,
                device));

        ProviderRepository providerRepository = new ProviderRepositoryJpa();
        final Provider provider = providerRepository.save(new Provider("submitTrainingRequest@nunum.me", UUID.randomUUID().toString(), true, device));

        RoleRepository roleRepository = new RoleRepositoryJpa();
        try {
            final Role role = roleRepository.save(new Role("provider"));
            role.addDevice(device);
            roleRepository.save(role);
        } catch (Exception e) {
            final Role role = roleRepository.findRole("provider");
            role.addDevice(device);
            roleRepository.save(role);
        }

        AlgorithmRepository algorithmRepository = new AlgorithmRepositoryJpa();
        Algorithm algorithm = algorithmRepository.save(new Algorithm("zSubmitTrainingRequest", "zSubmitTrainingRequest", "http://example.pt", false, device));

        AlgorithmProvider algorithmProvider = new AlgorithmProvider(provider, AlgorithmProvider.METHOD.GIT, new HashMap<>());
        algorithm.addProvider(algorithmProvider);
        algorithm = algorithmRepository.save(algorithm);

        algorithmProvider = algorithm.firstAlgorithmProvider().get();

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("algorithmId", algorithm.getId());
        payload.put("providerId", algorithmProvider.getId());

        final Response response = target("localization/" + localization.id() + "/train")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "submitTrainingRequest")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Algorithm is not approved for use", 403, response.getStatus());


        algorithm.setApproved(true);
        algorithmRepository.save(algorithm);

        final Response response1 = target("localization/" + localization.id() + "/train")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "submitTrainingRequest")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Create training", 200, response1.getStatus());


        final Response response2 = target("localization/" + localization.id() + "/train")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "submitTrainingRequest")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Re-Submit training", 200, response2.getStatus());


        payload.put("providerId", -1);
        final Response response3 = target("localization/" + localization.id() + "/train")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "submitTrainingRequest")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Not existing provider", 404, response3.getStatus());

    }

    @Test
    public void trainingStatus() {

        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "trainingStatus");


        final Localization localization = localizationRepository.save(new Localization("trainingStatus",
                "trainingStatus",
                0.0,
                0.0,
                false,
                device));

        ProviderRepository providerRepository = new ProviderRepositoryJpa();
        final Provider provider = providerRepository.save(new Provider("trainingStatus@nunum.me", UUID.randomUUID().toString(), true, device));

        RoleRepository roleRepository = new RoleRepositoryJpa();
        try {
            final Role role = roleRepository.save(new Role("provider"));
            role.addDevice(device);
            roleRepository.save(role);
        } catch (Exception e) {
            final Role role = roleRepository.findRole("provider");
            role.addDevice(device);
            roleRepository.save(role);
        }

        AlgorithmRepository algorithmRepository = new AlgorithmRepositoryJpa();
        Algorithm algorithm = algorithmRepository.save(new Algorithm("zTrainingStatus", "trainingStatus", "http://example.pt", false, device));

        AlgorithmProvider algorithmProvider = new AlgorithmProvider(provider, AlgorithmProvider.METHOD.GIT, new HashMap<>());
        algorithm.addProvider(algorithmProvider);
        algorithm = algorithmRepository.save(algorithm);

        algorithmProvider = algorithm.firstAlgorithmProvider().get();

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("algorithmId", algorithm.getId());
        payload.put("providerId", algorithmProvider.getId());

        final Response response = target("localization/" + localization.id() + "/train")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "trainingStatus")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Algorithm is not approved for use", 403, response.getStatus());


        algorithm.setApproved(true);
        algorithmRepository.save(algorithm);

        final Response response1 = target("localization/" + localization.id() + "/train")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "trainingStatus")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Create training", 200, response1.getStatus());
        final HashMap entity = response1.readEntity(HashMap.class);
        assertTrue("Valid id", entity.containsKey("id"));


        final Response response2 = target("localization/" + localization.id() + "/train/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "trainingStatus")
                .buildGet()
                .invoke();

        assertEquals("A valid response", 200, response2.getStatus());


        final Response response3 = target("localization/" + localization.id() + "/train/-1")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "trainingStatus")
                .buildGet()
                .invoke();

        assertEquals("Not found", 404, response3.getStatus());


        final Response response4 = target("localization/" + localization.id() + "/train/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "notTrainingStatus")
                .buildGet()
                .invoke();

        assertEquals("Forbidden training status access", 403, response4.getStatus());

    }

    @Test
    public void allTrainingStatus() {

        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "allTrainingStatus");


        final Localization localization = localizationRepository.save(new Localization("allTrainingStatus",
                "allTrainingStatus",
                0.0,
                0.0,
                false,
                device));

        ProviderRepository providerRepository = new ProviderRepositoryJpa();
        final Provider provider = providerRepository.save(new Provider("allTrainingStatus@nunum.me", UUID.randomUUID().toString(), true, device));

        RoleRepository roleRepository = new RoleRepositoryJpa();
        try {
            final Role role = roleRepository.save(new Role("provider"));
            role.addDevice(device);
            roleRepository.save(role);
        } catch (Exception e) {
            final Role role = roleRepository.findRole("provider");
            role.addDevice(device);
            roleRepository.save(role);
        }

        AlgorithmRepository algorithmRepository = new AlgorithmRepositoryJpa();
        Algorithm algorithm = algorithmRepository.save(new Algorithm("zTallTrainingStatus", "allTrainingStatus", "http://example.pt", true, device));

        AlgorithmProvider algorithmProvider = new AlgorithmProvider(provider, AlgorithmProvider.METHOD.GIT, new HashMap<>());
        algorithm.addProvider(algorithmProvider);
        algorithm = algorithmRepository.save(algorithm);

        algorithmProvider = algorithm.firstAlgorithmProvider().get();

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("algorithmId", algorithm.getId());
        payload.put("providerId", algorithmProvider.getId());


        algorithm.setApproved(true);
        algorithmRepository.save(algorithm);

        final Response response1 = target("localization/" + localization.id() + "/train")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "allTrainingStatus")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Create training", 200, response1.getStatus());


        final Response response2 = target("localization/" + localization.id() + "/train")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "allTrainingStatus")
                .header("timezone", "Europe/Lisbon")
                .buildGet()
                .invoke();
        assertEquals("A list of trainings", 200, response2.getStatus());
    }

    @Test
    public void deleteTrainingRequest() {


        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "deleteTrainingRequest");


        final Localization localization = localizationRepository.save(new Localization("deleteTrainingRequest",
                "deleteTrainingRequest",
                0.0,
                0.0,
                false,
                device));

        ProviderRepository providerRepository = new ProviderRepositoryJpa();
        final Provider provider = providerRepository.save(new Provider("deleteTrainingRequest@nunum.me", UUID.randomUUID().toString(), true, device));

        RoleRepository roleRepository = new RoleRepositoryJpa();
        try {
            final Role role = roleRepository.save(new Role("provider"));
            role.addDevice(device);
            roleRepository.save(role);
        } catch (Exception e) {
            final Role role = roleRepository.findRole("provider");
            role.addDevice(device);
            roleRepository.save(role);
        }

        AlgorithmRepository algorithmRepository = new AlgorithmRepositoryJpa();
        Algorithm algorithm = algorithmRepository.save(new Algorithm("ZDeleteTrainingRequest", "deleteTrainingRequest", "http://example.pt", true, device));

        AlgorithmProvider algorithmProvider = new AlgorithmProvider(provider, AlgorithmProvider.METHOD.GIT, new HashMap<>());
        algorithm.addProvider(algorithmProvider);
        algorithm = algorithmRepository.save(algorithm);

        algorithmProvider = algorithm.firstAlgorithmProvider().get();

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("algorithmId", algorithm.getId());
        payload.put("providerId", algorithmProvider.getId());


        algorithm.setApproved(true);
        algorithmRepository.save(algorithm);

        final Response response1 = target("localization/" + localization.id() + "/train")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deleteTrainingRequest")
                .buildPost(Entity.json(payload))
                .invoke();

        assertEquals("Create training", 200, response1.getStatus());
        final HashMap entity = response1.readEntity(HashMap.class);
        assertTrue("Valid id", entity.containsKey("id"));


        final Response response2 = target("localization/" + localization.id() + "/train/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deleteTrainingRequest")
                .buildDelete()
                .invoke();
        assertEquals("Delete completed", 200, response2.getStatus());

    }
}