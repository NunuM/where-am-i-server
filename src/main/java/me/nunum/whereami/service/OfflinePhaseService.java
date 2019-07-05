package me.nunum.whereami.service;

import me.nunum.whereami.framework.domain.Executable;
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
import me.nunum.whereami.service.notification.NotifyService;
import me.nunum.whereami.utils.AppConfig;

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

public class OfflinePhaseService extends Executable {

    private static final Logger LOGGER = Logger.getLogger(OfflinePhaseService.class.getSimpleName());


    public OfflinePhaseService() {
        super();
    }

    /**
     *
     */
    public Boolean call() {

        LOGGER.log(Level.INFO, "Starting OfflinePhaseService");

        try (final TaskRepository tasks = new TaskRepositoryJpa()) {

            final TrainingRepository trainings = new TrainingRepositoryJpa();
            final FingerprintRepository fingerprints = new FingerprintRepositoryJpa();

            final Stream<Task> openTasks = tasks.openTasks();

            openTasks.forEach(task -> {

                boolean wasLoopExhausted = true;

                if (task.getTraining().isHTTPProvider()) {

                    task.getTraining().trainingInProgress();

                    trainings.save(task.getTraining());

                    List<Fingerprint> fingerprintList = fingerprints
                            .fingerprintByLocalizationIdAndWithIdGreater(task.getTraining().localizationAssociated(), task.getCursor(), task.getBatchSize());

                    long size = fingerprintList.size();

                    while (size > 0) {

                        LOGGER.log(Level.INFO, String.format("Processing task %d. Current cursor: %d. Provider %d", task.getId(), task.getCursor(), task.getTraining().getAlgorithmProvider().getId()));

                        try {

                            this.flushPayload(task.getId(), false, fingerprintList, task.getTraining().providerProperties());

                        } catch (Exception e) {

                            LOGGER.log(Level.SEVERE, String.format("Sink Request fail. Processing task %d. Current cursor: %d. Provider %d", task.getId(), task.getCursor(), task.getTraining().getAlgorithmProvider().getId()), e);

                            if (e instanceof HTTPRequestError) {
                                this.warningProviderForRequestFailure(task.getTraining().getAlgorithmProvider().getEmail(), e.getMessage());
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
                                .fingerprintByLocalizationIdAndWithIdGreater(task.getTraining().localizationAssociated(), task.getCursor(), task.getBatchSize());

                        size = fingerprintList.size();
                    }
                }

                if (wasLoopExhausted) {
                    this.flushPayload(task.getId(),true, new ArrayList<>(), task.getTraining().providerProperties());
                    task.sinkFinish(Date.from(Instant.now()));
                    tasks.save(task);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not close resources", e);
        }

        return true;
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
    private boolean flushPayload(Long taskID, boolean isDrained, List<Fingerprint> fingerprints, Map<String, String> providerServiceProperties) {

        final String url = providerServiceProperties.get(AlgorithmProvider.HTTP_PROVIDER_INGESTION_URL_KEY);
        final Client client = ClientBuilder.newClient(AppConfig.getInstance().clientConfig());

        LOGGER.info(() -> String.format("Pushing %d samples for url %s", fingerprints.size(), url));

        HashMap<String, Object> payload = new HashMap<>(2);

        payload.put("id", taskID);
        payload.put("isDrained", isDrained);
        payload.put("fingerprints", fingerprints.stream().map(e -> e.toDTO().dtoValues()).collect(Collectors.toList()));

        try (final Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(payload, MediaType.APPLICATION_JSON))
                .invoke()) {

            if (response.getStatus() < 300 && response.getStatus() > 199) {
                return true;
            }

            final StringBuilder errorAsString = new StringBuilder();

            errorAsString.append(String.format("Response to %s return with status code %s.%n", url, response.getStatus()));

            errorAsString.append("Response headers:%n%n");

            response.getHeaders().forEach((k, v) ->
                    errorAsString.append(String.format("%-12s : %-5s %n", k, v.toString()))
            );

            if (response.hasEntity()) {
                final String entity = response.readEntity(String.class);
                errorAsString.append(String.format("%nThe server send this payload:%n %s%n", entity));
            } else {
                errorAsString.append("%nThe server not sent any payload%n");
            }

            throw new HTTPRequestError(errorAsString.toString());
        }

    }

    private void warningProviderForRequestFailure(final String mail, final String errorMessage) {
        LOGGER.log(Level.INFO, "Sending email: {0}", mail);
        LOGGER.log(Level.INFO, "Sending content: {0}", errorMessage);

        NotifyService.providerSinkError(mail, errorMessage);
    }

}
