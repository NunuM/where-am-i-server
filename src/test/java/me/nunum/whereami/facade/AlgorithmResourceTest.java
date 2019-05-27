package me.nunum.whereami.facade;

import me.nunum.whereami.model.Algorithm;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.persistance.AlgorithmRepository;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.jpa.AlgorithmRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.function.Function;

import static org.junit.Assert.*;

public class AlgorithmResourceTest extends JerseyTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

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
    protected Application configure() {
        return new ResourceConfig(AlgorithmResource.class);
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
}