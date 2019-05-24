package me.nunum.whereami.service;

import me.nunum.whereami.model.AlgorithmProvider;
import me.nunum.whereami.model.Fingerprint;
import me.nunum.whereami.model.Task;
import me.nunum.whereami.model.persistance.FingerprintRepository;
import me.nunum.whereami.model.persistance.TaskRepository;
import me.nunum.whereami.model.persistance.TrainingRepository;
import me.nunum.whereami.model.persistance.jpa.FingerprintRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.TaskRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.TrainingRepositoryJpa;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchedulerService implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(SchedulerService.class.getSimpleName());

    @Override
    public void run() {

        final TaskRepository tasks = new TaskRepositoryJpa();
        final TrainingRepository trainings = new TrainingRepositoryJpa();
        final FingerprintRepository fingerprints = new FingerprintRepositoryJpa();

        LOGGER.log(Level.INFO, "Starting SchedulerService service");

        Stream<Task> openTasks = tasks.openTasks();

        openTasks.forEach(task -> {

            if (task.getTraining().isHTTPProvider()) {
                int currentPage = 1;

                trainings.save(task.getTraining());

                List<Fingerprint> fingerprintList = fingerprints
                        .fingerprintByLocalizationIdAndWithIdGreater(task.getTraining().localizationAssociated(), task.getCursor(), currentPage);

                long size = fingerprintList.size();

                while (size > 0) {

                    this.flushPayload(fingerprintList, task.getTraining().providerProperties());

                    currentPage++;

                    fingerprintList = fingerprints
                            .fingerprintByLocalizationIdAndWithIdGreater(task.getTraining().localizationAssociated(), task.getCursor(), currentPage);

                    size = fingerprintList.size();
                }
            }

            task.setFinish(Date.from(Instant.now()));
            tasks.save(task);

        });

    }


    private boolean flushPayload(List<Fingerprint> payload, Map<String, String> properties) {

        final Client client = ClientBuilder.newClient();

        final Response response = client.target(properties.get(AlgorithmProvider.HTTP_PROVIDER_INGESTION_URL_KEY))
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(payload.stream().map(e->e.toDTO().dtoValues()).collect(Collectors.toList()), MediaType.APPLICATION_JSON))
                .invoke();

        return response.getStatus() < 300 && response.getStatus() > 199;

    }

    private void warningProviderForRequestFailure() {

    }

}
