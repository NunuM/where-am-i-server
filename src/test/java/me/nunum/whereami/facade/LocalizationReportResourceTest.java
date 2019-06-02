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

public class LocalizationReportResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(LocalizationResource.class, LocalizationReportResource.class).register(PrincipalInterceptor.class);
    }

    @Test
    public void localizationSpam() {


        LocalizationRepositoryJpa localizationRepository = new LocalizationRepositoryJpa();

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "localizationSpam");

        Localization localization = localizationRepository.save(new Localization("localizationSpam",
                "localizationSpam",
                0.0,
                0.0,
                false,
                device));


        HashMap<String, Object> report = new HashMap<>();
        report.put("id", localization.id());
        report.put("className", "Localization");

        // Create a valid report
        Response response = target("localization/" + localization.id().toString() + "/spam")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "localizationSpam")
                .buildPost(Entity.json(report))
                .invoke();

        assertEquals("Create a valid spam report", 200, response.getStatus());

        localization = localizationRepository.findById(localization.id()).get();
        assertEquals("Must have one reporter", 1, localization.getSpamReport().getReporters().size());


        // Not existing localization
        report.put("id", -11);
        Response response2 = target("localization/-10/spam")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "localizationSpam")
                .buildPost(Entity.json(report))
                .invoke();

        assertEquals("Not existing localization", 404, response2.getStatus());

    }
}