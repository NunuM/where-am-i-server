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
import me.nunum.whereami.service.exceptions.HTTPRequestError;

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

    /**
     *
     */
    @Override
    public void run() {

        final TaskRepository tasks = new TaskRepositoryJpa();
        final TrainingRepository trainings = new TrainingRepositoryJpa();
        final FingerprintRepository fingerprints = new FingerprintRepositoryJpa();

        LOGGER.log(Level.INFO, "Starting SchedulerService service");

        final Stream<Task> openTasks = tasks.openTasks();

        openTasks.forEach(task -> {

            boolean wasLoopExhausted = true;

            if (task.trainingInfo().isHTTPProvider()) {

                task.trainingInfo().trainingInProgress();

                trainings.save(task.trainingInfo());

                List<Fingerprint> fingerprintList = fingerprints
                        .fingerprintByLocalizationIdAndWithIdGreater(task.trainingInfo().localizationAssociated(), task.getCursor(), task.getBatchSize());

                long size = fingerprintList.size();

                while (size > 0) {

                    LOGGER.log(Level.INFO, String.format("Processing task %d. Current cursor: %d. Provider %d", task.getId(), task.getCursor(), task.trainingInfo().getAlgorithmProvider().getId()));

                    try {

                        this.flushPayload(task.getId(), fingerprintList, task.trainingInfo().providerProperties());

                    } catch (Exception e) {

                        LOGGER.log(Level.SEVERE, String.format("Sink Request fail. Processing task %d. Current cursor: %d. Provider %d", task.getId(), task.getCursor(), task.trainingInfo().getAlgorithmProvider().getId()), e);

                        if (e instanceof HTTPRequestError) {
                            this.warningProviderForRequestFailure(task.trainingInfo().getAlgorithmProvider().getEmail(), e.getMessage());
                        }

                        wasLoopExhausted = false;
                        break;
                    }

                    fingerprintList
                            .stream()
                            .max(Comparator.comparing(Fingerprint::getId))
                            .map(Fingerprint::getId)
                            .ifPresent(e -> {
                                task.setCursor(e);
                                tasks.save(task);
                            });

                    fingerprintList = fingerprints
                            .fingerprintByLocalizationIdAndWithIdGreater(task.trainingInfo().localizationAssociated(), task.getCursor(), task.getBatchSize());

                    size = fingerprintList.size();
                }
            }

            if (wasLoopExhausted) {
                task.sinkFinish(Date.from(Instant.now()));
                tasks.save(task);
            }

        });

    }

    /**
     * Send a batch of fingerprints to a HTTP server
     *
     * @param taskID
     * @param fingerprints
     * @param providerServiceProperties
     * @return boolean
     * @throws HTTPRequestError
     */
    private boolean flushPayload(Long taskID, List<Fingerprint> fingerprints, Map<String, String> providerServiceProperties) {

        final String url = providerServiceProperties.get(AlgorithmProvider.HTTP_PROVIDER_INGESTION_URL_KEY);
        final Client client = ClientBuilder.newClient();

        HashMap<String, Object> payload = new HashMap<>(2);

        payload.put("id", taskID);
        payload.put("fingerprints", fingerprints.stream().map(e -> e.toDTO().dtoValues()).collect(Collectors.toList()));

        try (Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(payload, MediaType.APPLICATION_JSON))
                .invoke()) {

            if (response.getStatus() < 300 && response.getStatus() > 199) {
                return true;
            }

            final StringBuilder errorAsString = new StringBuilder();

            errorAsString.append(String.format("Response to %s return with status code %s.\n", url, response.getStatus()));

            errorAsString.append("Response headers:\n\n");

            response.getHeaders().forEach((k, v) -> {
                errorAsString.append(String.format("%-12s : %-5s \n", k, v.toString()));
            });

            if (response.hasEntity()) {
                final String entity = response.readEntity(String.class);
                errorAsString.append(String.format("\nThe server send this payload:\n %s\n", entity));
            } else {
                errorAsString.append("\nThe server not sent any payload\n");
            }

            throw new HTTPRequestError(errorAsString.toString());
        }

    }

    private void warningProviderForRequestFailure(final String mail, final String errorMesssage) {

        LOGGER.log(Level.INFO, "Sending email" + mail + "\n");
        LOGGER.log(Level.INFO, "Sending content" + errorMesssage + "\n");
    }

}
