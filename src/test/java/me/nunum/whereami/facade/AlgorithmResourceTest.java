package me.nunum.whereami.facade;

import me.nunum.whereami.framework.interceptor.PrincipalInterceptor;
import me.nunum.whereami.model.*;
import me.nunum.whereami.model.persistance.*;
import me.nunum.whereami.model.persistance.jpa.*;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AlgorithmResourceTest extends JerseyTest {

    @BeforeClass
    public static void insertData() {
        final DeviceRepository deviceRepository = new DeviceRepositoryJpa();

        Device device = deviceRepository.findOrPersist(() -> "Test");

        final AlgorithmRepository repository = new AlgorithmRepositoryJpa();

        for (int i = 0; i < 40; i++) {
            repository.save(new Algorithm("Name" + i, "Author" + i, "http://paper.com/paper" + i, true, device));
        }


        for (int j = 40; j < 80; j++) {
            repository.save(new Algorithm("Name" + j, "Author" + j, "http://paper.com/paper" + j, false, device));
        }

    }


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(AlgorithmResource.class).register(PrincipalInterceptor.class);
    }

    @Test
    public void testAvailableAlgorithmWithPagination() {

        Function<Integer, Response> makeRequest = (page) -> target("/algorithm")
                .queryParam("page", page)
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildGet()
                .invoke();

        Response response = makeRequest.apply(1);

        //Inserted 40 approved rows, this must be true
        assertEquals(200, response.getStatus());
        assertTrue(response.hasEntity());

        //GET HTTP request must be safe, thus, all non valid integers passed on page query
        //will act as the first page has been requested
        Response response1 = makeRequest.apply(-1);
        assertEquals(200, response.getStatus());
        assertTrue(response.hasEntity());

        //Inserted 40 approved rows
        List<Object> objects = response.readEntity(Vector.class);
        assertEquals("Must have", 20, objects.size());

        //Inserted 40 approved rows the subsequent pages must be empty
        Response response3 = makeRequest.apply(10);
        assertEquals("[]", response3.readEntity(String.class));
    }

    @Test
    public void testGetAlgorithm() {

        final DeviceRepository deviceRepository = new DeviceRepositoryJpa();

        Device device = deviceRepository.findOrPersist(() -> "testGetAlgorithm");

        final AlgorithmRepository repository = new AlgorithmRepositoryJpa();

        Algorithm algorithm = repository.save(new Algorithm("testGetAlgorithm", "testGetAlgorithm", "http://paper.com/paper", true, device));


        Function<String, Response> makeRequest = (aId) -> target("/algorithm/" + aId)
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildGet()
                .invoke();


        final Response response = makeRequest.apply(algorithm.getId().toString());

        assertEquals(200, response.getStatus());

        final Response response1 = makeRequest.apply("-1");
        assertEquals(404, response1.getStatus());

    }

    @Test
    public void testNewAlgorithm() {

        Function<HashMap<String, Object>, Response> makeRequest = (payload) -> target("/algorithm")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPost(Entity.json(payload))
                .invoke();

        HashMap<String, Object> payload = new HashMap<>();

        payload.put("name", "NameTest");
        payload.put("authorName", "AuthorTest");
        //Invalid URL
        payload.put("paperURL", "NonValidUrl");

        Response response = makeRequest.apply(payload);
        assertTrue("Invalid Request", response.getStatus() == 400);
        response.close();

        //Valid URL
        payload.put("paperURL", "http://example.com");
        Response response1 = makeRequest.apply(payload);
        assertTrue("Valid payload", response1.getStatus() == 200);
        response1.close();

        //Try to create same entity
        Response response2 = makeRequest.apply(payload);
        assertTrue("Duplicate entity", response2.getStatus() == 409);
        response2.close();
    }

    @Test
    public void testUpdateAlgorithm() {

        Function<HashMap<String, Object>, HashMap<String, Object>> makeRequest = (payload) -> target("/algorithm")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPost(Entity.json(payload))
                .invoke(HashMap.class);

        //Create new valid entity
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "NameTest5");
        payload.put("authorName", "AuthorTest4");
        payload.put("paperURL", "http://example.com");
        HashMap<String, Object> response = makeRequest.apply(payload);
        assertTrue(response.containsKey("id"));

        //Change and persist entity with a new name
        response.put("name", "NewNameTest");
        HashMap algorithmUpdated = target("/algorithm/" + response.get("id"))
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPut(Entity.json(response))
                .invoke(HashMap.class);
        assertEquals("Must reply with the updated name", algorithmUpdated.get("name"), "NewNameTest");

        //Try update a non existing entity
        Response response1 = target("/algorithm/100000")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPut(Entity.json(response))
                .invoke();
        assertTrue(response1.getStatus() == 404);
        response1.close();

        //Try update algorithm not being the one who that created
        Response response2 = target("/algorithm/" + response.get("id"))
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "APP-TEST")
                .buildPut(Entity.json(response))
                .invoke();
        assertTrue("Forbidden reply", response2.getStatus() == 403);
        response2.close();
    }

    /**
     * Only Admin can execute this action
     * due the cascading that may be involved.
     * <p>
     * This is test will be successful action because
     * roles validation module is not loaded.
     */
    @Test
    public void deleteAlgorithm() {

        Function<HashMap<String, Object>, HashMap<String, Object>> makeRequest = (payload) -> target("/algorithm")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPost(Entity.json(payload))
                .invoke(HashMap.class);

        //Create new valid entity
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "NameTest1");
        payload.put("authorName", "AuthorTest");
        payload.put("paperURL", "http://example.com");
        HashMap<String, Object> response = makeRequest.apply(payload);
        assertTrue(response.containsKey("id"));

        final Response response1 = target("/algorithm/" + response.get("id"))
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "APP-TEST")
                .buildDelete()
                .invoke();
        assertTrue("We must delete entity", response1.getStatus() == 200);
    }

    @Test
    public void approveAlgorithm() {

        Function<HashMap<String, Object>, HashMap<String, Object>> makeRequest = (payload) -> target("/algorithm")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPost(Entity.json(payload))
                .invoke(HashMap.class);

        //Create new valid entity
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "ZName1Test");
        payload.put("authorName", "AuthorTest");
        payload.put("paperURL", "http://example.com");
        HashMap<String, Object> response = makeRequest.apply(payload);
        assertTrue(response.containsKey("id"));

        HashMap<String, Object> approvePayload = new HashMap<>(1);
        approvePayload.put("approved", true);

        final HashMap<String, Object> result = target("/algorithm/" + response.get("id") + "/approval")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "APP-TEST")
                .buildPut(Entity.json(approvePayload))
                .invoke(HashMap.class);

        assertTrue("Same id", response.get("id").equals(result.get("id")));
        assertTrue("But Approved", result.get("isApproved").toString().equals("true"));

    }

    @Test
    public void deleteAlgorithmProviderWithQueuedTask() {

        Function<HashMap<String, Object>, HashMap<String, Object>> makeRequest = (payload) -> target("/algorithm")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPost(Entity.json(payload))
                .invoke(HashMap.class);

        //Create new valid algorithm entity
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "ZDeleteAlgorithmProviderDeleteAlgorithmProviderWithQueuedTask");
        payload.put("authorName", "AuthorTest");
        payload.put("paperURL", "http://example.com");
        HashMap<String, Object> response = makeRequest.apply(payload);
        assertTrue(response.containsKey("id"));

        // Register a device as provider
        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "deleteAlgorithmProviderWithQueuedTask");

        // Activate provider
        ProviderRepository providerRepository = new ProviderRepositoryJpa();
        providerRepository.save(new Provider("deleteAlgorithmProviderWithQueuedTask@nunum.me", UUID.randomUUID().toString(), true, device));


        // Create algorithm provider entity
        HashMap<String, Object> validValidPayload = new HashMap<>(2);
        validValidPayload.put("method", "git");
        HashMap<String, Object> properies = new HashMap<>(1);
        properies.put(AlgorithmProvider.GIT_PROVIDER_URL_KEY, "http://example.com");
        validValidPayload.put("properties", properies);

        HashMap<String, Object> response1 = target("/algorithm/" + response.get("id") + "/provider/")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deleteAlgorithmProviderWithQueuedTask")
                .buildPost(Entity.json(validValidPayload))
                .invoke(HashMap.class);
        assertTrue("A valid provider", response1.containsKey("id"));


        // Find recently created provider
        AlgorithmProviderRepository algorithmProviderRepository = new AlgorithmProviderRepositoryJpa();
        final AlgorithmProvider provider = algorithmProviderRepository.findById(Long.valueOf(response1.get("id").toString())).get();

        // Create new localization
        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();
        final Localization localization = localizationRepository.save(new Localization("deleteAlgorithmProviderWithQueuedTask", "deleteAlgorithmProviderWithQueuedTask", device));

        // Find algorithm previously created
        AlgorithmRepository algorithmRepository = new AlgorithmRepositoryJpa();
        final Algorithm algorithm = algorithmRepository.findById(Long.valueOf(response.get("id").toString())).get();

        // Add a new training task to a given provider that implements a specific algorithm
        localization.addTraining(new Training(algorithm, provider, localization));
        localizationRepository.save(localization);

        // Delete algorithm provider
        Response response3 = target("/algorithm/" + response.get("id") + "/provider/" + provider.getId())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deleteAlgorithmProviderWithQueuedTask")
                .buildDelete()
                .invoke();
        assertEquals("Delete success",  200, response3.getStatus());

    }

    @Test
    public void deleteAlgorithmProvider() {

        Function<HashMap<String, Object>, HashMap<String, Object>> makeRequest = (payload) -> target("/algorithm")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPost(Entity.json(payload))
                .invoke(HashMap.class);

        //Create new valid algorithm entity
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "ZDeleteAlgorithmProvider");
        payload.put("authorName", "AuthorTest");
        payload.put("paperURL", "http://example.com");
        HashMap<String, Object> response = makeRequest.apply(payload);
        assertTrue(response.containsKey("id"));

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "deleteAlgorithmProvider");
        Device device2 = deviceRepository.findOrPersist(() -> "deleteProvider");

        ProviderRepository providerRepository = new ProviderRepositoryJpa();
        providerRepository.save(new Provider("deleteAlgorithmProvider@nunum.me", UUID.randomUUID().toString(), true, device));
        providerRepository.save(new Provider("deleteAlgorithmProvider2@nunum.me", UUID.randomUUID().toString(), true, device2));

        // Create algorithm provider entity
        HashMap<String, Object> validValidPayload = new HashMap<>(2);
        validValidPayload.put("method", "git");
        HashMap<String, Object> properies = new HashMap<>(1);
        properies.put(AlgorithmProvider.GIT_PROVIDER_URL_KEY, "http://example.com");
        validValidPayload.put("properties", properies);

        Response response2 = target("/algorithm/" + response.get("id") + "/provider/")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deleteAlgorithmProvider")
                .buildPost(Entity.json(validValidPayload))
                .invoke();

        assertTrue("A valid provider", response2.getStatus() == 200);
        final HashMap entity = response2.readEntity(HashMap.class);
        assertTrue("Must have an ID", entity.containsKey("id"));

        // Provider without ownership
        Response response5 = target("/algorithm/" + response.get("id") + "/provider/" + entity.get("id"))
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deleteProvider")
                .buildDelete()
                .invoke();
        assertTrue("Not owner", response5.getStatus() == 403);

        // Delete algorithm provider
        Response response3 = target("/algorithm/" + response.get("id") + "/provider/" + entity.get("id"))
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deleteAlgorithmProvider")
                .buildDelete()
                .invoke();
        assertTrue("Delete success", response3.getStatus() == 200);

        // Not found
        Response response4 = target("/algorithm/" + response.get("id") + "/provider/" + "-1")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "deleteAlgorithmProvider")
                .buildDelete()
                .invoke();
        assertTrue("Non existing entity", response4.getStatus() == 404);

    }

    @Test
    public void addAlgorithmProvider() {

        Function<HashMap<String, Object>, HashMap<String, Object>> makeRequest = (payload) -> target("/algorithm")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPost(Entity.json(payload))
                .invoke(HashMap.class);

        //Create new valid algorithm entity
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "ZAddAlgorithmProvider");
        payload.put("authorName", "AuthorTest");
        payload.put("paperURL", "http://example.com");
        HashMap<String, Object> response = makeRequest.apply(payload);
        assertTrue(response.containsKey("id"));


        // Requester is not a Provider
        HashMap<String, String> newProviderPayload = new HashMap<>();
        payload.put("method", "git");

        Response response1 = target("algorithm/" + response.get("id") + "/provider")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPost(Entity.json(newProviderPayload))
                .invoke();

        assertTrue("Device not a provider", response1.getStatus() == 403);
        response1.close();


        // Register a new provider not confirmed email
        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "provider");

        ProviderRepository providerRepository = new ProviderRepositoryJpa();
        Provider provider = providerRepository.save(new Provider("teste12345@nunum.me", UUID.randomUUID().toString(), false, device));


        Response response2 = target("algorithm/" + response.get("id") + "/provider")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "provider")
                .buildPost(Entity.json(newProviderPayload))
                .invoke();

        assertTrue("Device is a provider however not confirmed the email", response2.getStatus() == 403);
        response2.close();


        // Confirmed provider email but invalid payload
        provider.providerHasConfirmedEmail();
        providerRepository.save(provider);

        HashMap<String, Object> theInvalidValidPayload = new HashMap<>(1);
        theInvalidValidPayload.put("method", "git");

        Response response3 = target("/algorithm/" + response.get("id") + "/provider/")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "provider")
                .buildPost(Entity.json(theInvalidValidPayload))
                .invoke();

        assertTrue("Properties are missing", response3.getStatus() == 400);


        // Create algorithm provider entity
        HashMap<String, Object> validValidPayload = new HashMap<>(2);
        validValidPayload.put("method", "git");
        HashMap<String, Object> properies = new HashMap<>(1);
        properies.put(AlgorithmProvider.GIT_PROVIDER_URL_KEY, "http://example.com");
        validValidPayload.put("properties", properies);

        Response response4 = target("/algorithm/" + response.get("id") + "/provider/")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "provider")
                .buildPost(Entity.json(validValidPayload))
                .invoke();

        assertTrue("A valid provider", response4.getStatus() == 200);


        // Try to create again same algorithm provider entity
        Response response5 = target("/algorithm/" + response.get("id") + "/provider/")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "provider")
                .buildPost(Entity.json(validValidPayload))
                .invoke();

        assertTrue("Duplicate provider", response5.getStatus() == 409);


        // Why not?
        Response response6 = target("/algorithm/-1/provider/")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "provider")
                .buildPost(Entity.json(validValidPayload))
                .invoke();

        assertTrue("Algorithm not found", response6.getStatus() == 404);

    }


    @Test
    public void updateProvider() throws Exception {

        Function<HashMap<String, Object>, HashMap<String, Object>> makeRequest = (payload) -> target("/algorithm")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPost(Entity.json(payload))
                .invoke(HashMap.class);

        //Create new valid algorithm entity
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("name", "ZDeleteAlgorithmProviderUpdateProvider");
        payload.put("authorName", "AuthorTest");
        payload.put("paperURL", "http://example.com");
        HashMap<String, Object> response = makeRequest.apply(payload);
        assertTrue(response.containsKey("id"));

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "updateProvider");
        Device device2 = deviceRepository.findOrPersist(() -> "updateProvider2");

        ProviderRepository providerRepository = new ProviderRepositoryJpa();
        providerRepository.save(new Provider("updateProvider@nunum.me", UUID.randomUUID().toString(), true, device));
        providerRepository.save(new Provider("updateProvider2@nunum.me", UUID.randomUUID().toString(), true, device2));

        // Create algorithm provider entity
        HashMap<String, Object> validValidPayload = new HashMap<>(2);
        validValidPayload.put("method", "git");
        HashMap<String, Object> properies = new HashMap<>(1);
        properies.put(AlgorithmProvider.GIT_PROVIDER_URL_KEY, "http://example.com");
        validValidPayload.put("properties", properies);

        Response response2 = target("/algorithm/" + response.get("id") + "/provider/")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "updateProvider")
                .buildPost(Entity.json(validValidPayload))
                .invoke();

        assertTrue("A valid provider", response2.getStatus() == 200);
        final HashMap entity = response2.readEntity(HashMap.class);
        assertTrue("Must have an ID", entity.containsKey("id"));
        final HashMap<String, Object> persistedProperties = (HashMap<String, Object>) entity.get("properties");
        assertTrue("Same url", persistedProperties.get(AlgorithmProvider.GIT_PROVIDER_URL_KEY).toString().equals("http://example.com"));

        // Add new property and change previous url without specify method
        HashMap<String, Object> entityToUpdate = new HashMap<>();
        HashMap<String, Object> entityPropertiesToUpdate = new HashMap<>();
        entityPropertiesToUpdate.put("new", "http://newexample.com");
        entityPropertiesToUpdate.put(AlgorithmProvider.GIT_PROVIDER_URL_KEY, "http://example.net");
        entityToUpdate.put("properties", entityPropertiesToUpdate);

        HashMap<String, Object> updateResponse = target("/algorithm/" + response.get("id") + "/provider/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "updateProvider")
                .buildPut(Entity.json(entityToUpdate))
                .invoke(HashMap.class);

        AlgorithmProviderRepository algorithmProviderRepository = new AlgorithmProviderRepositoryJpa();
        final AlgorithmProvider provider = algorithmProviderRepository.findById(Long.valueOf(updateResponse.get("id").toString())).get();
        algorithmProviderRepository.close();

        assertTrue("Must have new key", provider.getProperties().containsKey("new"));
        assertTrue("Url must be different", provider.getProperties().get(AlgorithmProvider.GIT_PROVIDER_URL_KEY).toString().equals("http://example.net"));


        // Add new property and change previous url specifying the method
        HashMap<String, Object> entityToUpdate1 = new HashMap<>();
        HashMap<String, Object> entityPropertiesToUpdate1 = new HashMap<>();
        entityPropertiesToUpdate1.put("old", "http://oldexample.com");
        entityPropertiesToUpdate1.put(AlgorithmProvider.GIT_PROVIDER_URL_KEY, "http://example.pt");
        entityToUpdate1.put("properties", entityPropertiesToUpdate1);
        entityToUpdate1.put("method", "git");


        HashMap<String, Object> updateResponse1 = target("/algorithm/" + response.get("id") + "/provider/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "updateProvider")
                .buildPut(Entity.json(entityToUpdate1))
                .invoke(HashMap.class);

        AlgorithmProviderRepository algorithmProviderRepository1 = new AlgorithmProviderRepositoryJpa();
        final AlgorithmProvider provider1 = algorithmProviderRepository1.findById(Long.valueOf(updateResponse.get("id").toString())).get();

        assertTrue("Must have new key", provider1.getProperties().containsKey("new"));
        assertTrue("Must have old key", provider1.getProperties().containsKey("old"));
        assertTrue("Url must be different", provider1.getProperties().get(AlgorithmProvider.GIT_PROVIDER_URL_KEY).toString().equals("http://example.pt"));

        // Not Found
        Response updateResponse2 = target("/algorithm/" + response.get("id") + "/provider/-1")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "updateProvider")
                .buildPut(Entity.json(entityToUpdate1))
                .invoke();
        assertTrue("Not found", updateResponse2.getStatus() == 404);

        // Unsupported method
        entityToUpdate1.put("method", "ftp");
        Response updateResponse3 = target("/algorithm/" + response.get("id") + "/provider/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "updateProvider")
                .buildPut(Entity.json(entityToUpdate1))
                .invoke();
        assertTrue("Bad request", updateResponse3.getStatus() == 400);


        // Unsupported method
        entityToUpdate1.put("method", "ftp");
        Response updateResponse4 = target("/algorithm/" + response.get("id") + "/provider/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "updateProvider2")
                .buildPut(Entity.json(entityToUpdate1))
                .invoke();
        assertTrue("Forbidden", updateResponse4.getStatus() == 403);


        // Change method missing one key
        entityToUpdate1.put("method", "http");
        Response updateResponse5 = target("/algorithm/" + response.get("id") + "/provider/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "updateProvider")
                .buildPut(Entity.json(entityToUpdate1))
                .invoke();
        assertTrue("Change method but missing key", updateResponse5.getStatus() == 400);


        // Change method with all keys
        entityPropertiesToUpdate1.put(AlgorithmProvider.HTTP_PROVIDER_INGESTION_URL_KEY, "http://example.com");
        entityPropertiesToUpdate1.put(AlgorithmProvider.HTTP_PROVIDER_PREDICTION_URL_KEY, "http://example.com");
        Response updateResponse6 = target("/algorithm/" + response.get("id") + "/provider/" + entity.get("id").toString())
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "updateProvider")
                .buildPut(Entity.json(entityToUpdate1))
                .invoke();
        assertTrue("Change method", updateResponse6.getStatus() == 200);

    }
}