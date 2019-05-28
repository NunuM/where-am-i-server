package me.nunum.whereami.facade;

import me.nunum.whereami.framework.interceptor.PrincipalInterceptor;
import me.nunum.whereami.model.Algorithm;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.persistance.AlgorithmRepository;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.jpa.AlgorithmRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.request.NewAlgorithmRequest;
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
import java.util.function.Function;

import static org.junit.Assert.*;

public class AlgorithmResourceTest extends JerseyTest {

    @BeforeClass
    public static void insertData() {
        final DeviceRepository deviceRepository = new DeviceRepositoryJpa();

        Device device = deviceRepository.findOrPersist(() -> "Test");

        final AlgorithmRepository repository = new AlgorithmRepositoryJpa();

        int i = 0;
        for (i = 0; i < 40; i++) {
            repository.save(new Algorithm("Name" + i, "Author" + i, "http://paper.com/paper" + i, true, device));
        }


        for (int j = 0; j < 40; j++) {
            repository.save(new Algorithm("Name" + (i + j), "Author" + (i + j), "http://paper.com/paper" + (i + j), false, device));
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
        assertEquals(response.getStatus(), 200);
        assertTrue(response.hasEntity());

        //GET HTTP request must be safe, thus, all non valid integers passed on page query
        //will act as the first page has been requested
        Response response1 = makeRequest.apply(-1);
        assertEquals(response.getStatus(), 200);
        assertTrue(response.hasEntity());

        //Inserted 40 approved rows the last of the first page must be the row with 19
        final String page1 = response.readEntity(String.class);
        assertTrue(page1.endsWith("Author19\"}]"));
        assertEquals(page1, response1.readEntity(String.class));

        //Inserted 40 approved rows the first of the second page must be the row with 19
        Response response2 = makeRequest.apply(2);
        String page2 = response2.readEntity(String.class);
        assertTrue(page2.startsWith("[{\"name\":\"Name20\""));
        assertNotSame(page1, page2);

        //Inserted 40 approved rows the subsequent pages must be empty
        Response response3 = makeRequest.apply(3);
        assertEquals("[]", response3.readEntity(String.class));
    }

    @Test
    public void testGetAlgorithm() {

        Function<String, Response> makeRequest = (aId) -> target("/algorithm/" + aId)
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildGet()
                .invoke();


        final Response response = makeRequest.apply("39");

        assertTrue(response.getStatus() == 200);
        assertTrue(response.readEntity(String.class).contains("39"));

        final Response response1 = makeRequest.apply("1000");
        assertTrue(response1.getStatus() == 404);

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
        payload.put("paperURL", "http://google.pt");
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
        payload.put("name", "NameTest");
        payload.put("authorName", "AuthorTest");
        payload.put("paperURL", "http://google.pt");
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
     *
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
        payload.put("name", "NameTest");
        payload.put("authorName", "AuthorTest");
        payload.put("paperURL", "http://google.pt");
        HashMap<String, Object> response = makeRequest.apply(payload);
        assertTrue(response.containsKey("id"));

        target("/algorithm/" + response.get("id"))
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "APP-TEST")
                .buildPut(Entity.json(response))
                .invoke();




    }
}