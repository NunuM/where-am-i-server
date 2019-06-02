package me.nunum.whereami.facade;

import me.nunum.whereami.framework.interceptor.PrincipalInterceptor;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.Provider;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.LocalizationRepository;
import me.nunum.whereami.model.persistance.PositionRepository;
import me.nunum.whereami.model.persistance.ProviderRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.LocalizationRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.PostitionRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.ProviderRepositoryJpa;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class FingerprintResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(FingerprintResource.class).register(PrincipalInterceptor.class);
    }


    @Test
    public void collectFingerprint() {

        List<HashMap<String, Object>> payload = new ArrayList<>();

        DeviceRepository deviceRepository = new DeviceRepositoryJpa();
        Device device = deviceRepository.findOrPersist(() -> "collectFingerprint");
        Device device1 = deviceRepository.findOrPersist(() -> "notCollectFingerprint");

        ProviderRepository providerRepository = new ProviderRepositoryJpa();
        providerRepository.save(new Provider("collectFingerprint@nunum.me", UUID.randomUUID().toString(), true, device));

        LocalizationRepository localizationRepository = new LocalizationRepositoryJpa();
        Localization localization = localizationRepository.save(new Localization("collectFingerprint", "collectFingerprint", device));
        Localization localization1 = localizationRepository.save(new Localization("notCollectFingerprint", "notCollectFingerprint", device1));

        PositionRepository positionRepository = new PostitionRepositoryJpa();
        Position position = positionRepository.save(new Position("collectFingerprint", localization));
        Position position1 = positionRepository.save(new Position("notCollectFingerprint", localization1));


        HashMap<String, Object> sample1 = new HashMap<>();

        sample1.put("bssid", "bssid" + 1);
        sample1.put("ssid", "ssid" + 1);
        sample1.put("levelDBM", 1);
        sample1.put("centerFreq0", 1);
        sample1.put("centerFreq1", 1);
        sample1.put("channelWidth", 1);
        sample1.put("frequency", 0);
        sample1.put("timeStamp", String.valueOf(Instant.now().getEpochSecond()));
        sample1.put("buildId", 1);
        sample1.put("floorId", 1);
        sample1.put("positionId", position1.id());
        sample1.put("localizationId", localization1.id());

        payload.add(sample1);

        for (int i = 10; i < 20; i++) {

            HashMap<String, Object> sample = new HashMap<>();

            sample.put("bssid", "bssid" + i);
            sample.put("ssid", "ssid" + i);
            sample.put("levelDBM", i);
            sample.put("centerFreq0", i);
            sample.put("centerFreq1", i);
            sample.put("channelWidth", i);
            sample.put("frequency", 0);
            sample.put("timeStamp", String.valueOf(Instant.now().getEpochSecond()));
            sample.put("buildId", i);
            sample.put("floorId", i);
            sample.put("positionId", position.id());
            sample.put("localizationId", localization.id());

            payload.add(sample);
        }




        Response response = target("fingerprint")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "collectFingerprint")
                .buildPost(Entity.json(payload))
                .invoke();

        assertTrue("Must persist", response.getStatus() == 200);
    }

    @Test
    public void collectFingerprintForANonExistingLocalization() {

        List<HashMap<String, Object>> payload = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            HashMap<String, Object> sample = new HashMap<>();

            sample.put("bssid", "bssid" + i);
            sample.put("ssid", "ssid" + i);
            sample.put("levelDBM", i);
            sample.put("centerFreq0", i);
            sample.put("centerFreq1", i);
            sample.put("channelWidth", i);
            sample.put("frequency", 0);
            sample.put("timeStamp", String.valueOf(Instant.now().getEpochSecond()));
            sample.put("buildId", i);
            sample.put("floorId", i);
            sample.put("positionId", i);
            sample.put("localizationId", i);

            payload.add(sample);
        }

        Response response = target("fingerprint")
                .request(MediaType.APPLICATION_JSON)
                .header("X-APP", "Test")
                .buildPost(Entity.json(payload))
                .invoke();

        assertTrue("Must persist", response.getStatus() == 200);
    }
}