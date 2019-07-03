package me.nunum.whereami.service;

import me.nunum.whereami.framework.domain.Executable;
import me.nunum.whereami.model.AlgorithmProvider;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Prediction;
import me.nunum.whereami.model.Training;
import me.nunum.whereami.model.persistance.PredictionRepository;
import me.nunum.whereami.model.persistance.jpa.PredictionRepositoryJpa;
import me.nunum.whereami.model.request.FingerprintSample;
import me.nunum.whereami.utils.AppConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OnlinePhaseService extends Executable {

    private static final Logger LOGGER = Logger.getLogger(OnlinePhaseService.class.getSimpleName());

    private final Localization localization;
    private final List<FingerprintSample> samples;
    private final Long requestId;
    private final PredictionRepository predictionRepository;

    public OnlinePhaseService(Localization localization, Long requestId, List<FingerprintSample> samples) {
        super();
        this.localization = localization;
        this.samples = samples;
        this.predictionRepository = new PredictionRepositoryJpa();
        this.requestId = requestId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Boolean call() throws Exception {

        final List<Training> trainings = this.localization
                .getTrainings()
                .stream()
                .filter(Training::isFinished)
                .collect(Collectors.toList());

        trainings
                .stream()
                .filter(Training::isHTTPProvider)
                .parallel()
                .forEach(e -> {

                    final Map<String, String> providerProperties = e.providerProperties();

                    final String url = providerProperties.get(AlgorithmProvider.HTTP_PROVIDER_PREDICTION_URL_KEY);

                    final Client client = ClientBuilder.newClient(AppConfig.getInstance().clientConfig());

                    final HashMap<String, Object> payload = new HashMap<>(2);
                    payload.put("localizationId", localization.id());
                    payload.put("samples", samples);

                    try (final Response response = client.target(url)
                            .request(MediaType.APPLICATION_JSON)
                            .header("X-APP", localization.getOwner().instanceId())
                            .buildPost(Entity.entity(payload, MediaType.APPLICATION_JSON))
                            .invoke()) {

                        if (response.getStatus() == 200
                                && response.hasEntity()) {

                            final HashMap<String, Object> entity = response.readEntity(HashMap.class);

                            final String positionIdKey = "positionId";
                            final String accuracyKey = "accuracy";

                            if (entity.containsKey(positionIdKey)) {

                                final long positionPredicated = ((BigDecimal) entity.get(positionIdKey)).longValue();

                                if (positionPredicated != 0L) {

                                    final Float accuracy = ((BigDecimal) entity.getOrDefault(accuracyKey, 0f)).floatValue();

                                    final Prediction prediction = new Prediction(
                                            requestId,
                                            localization.id(),
                                            positionPredicated,
                                            localization.positionLabelById(positionPredicated),
                                            accuracy,
                                            e.getAlgorithmProvider().getId());

                                    this.predictionRepository.save(prediction);
                                }
                            }
                        }
                    }

                });

        try {
            this.predictionRepository.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not close resources", e);
        }

        return true;
    }

}
