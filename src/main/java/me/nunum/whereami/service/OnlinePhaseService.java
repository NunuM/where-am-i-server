package me.nunum.whereami.service;

import me.nunum.whereami.framework.domain.Executable;
import me.nunum.whereami.model.*;
import me.nunum.whereami.model.persistance.PredictionRepository;
import me.nunum.whereami.model.persistance.jpa.LocalizationRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.PositionRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.PredictionRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.TrainingRepositoryJpa;
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

    private final Long localizationId;
    private final List<FingerprintSample> samples;
    private final Long requestId;

    public OnlinePhaseService(Long localizationId, Long requestId, List<FingerprintSample> samples) {
        super();
        this.localizationId = localizationId;
        this.samples = samples;
        this.requestId = requestId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Boolean call() throws Exception {

        final PredictionRepository predictionRepository = new PredictionRepositoryJpa();
        final LocalizationRepositoryJpa localizationRepository = new LocalizationRepositoryJpa();

        final Localization localization = localizationRepository.findById(this.localizationId).get();

        final TrainingRepositoryJpa trainingRepository = new TrainingRepositoryJpa();

        final List<Training> trainings = trainingRepository.findByLocalization(localization);

        final PositionRepositoryJpa positionRepository = new PositionRepositoryJpa();

        LOGGER.info("Collect training for the localization "
                + localization.id() + ":"
                + localization.getTrainings().size() + ":"
                + localization.getPositionList().size());

        LOGGER.info("FROM bd" + trainings.size());

        localization.getTrainings().forEach(e -> LOGGER.info("Training " + e.isHTTPProvider()));

        final List<HashMap<String, Object>> samplesList = samples.stream().map(FingerprintSample::values).collect(Collectors.toList());

        trainings
                .stream()
                .filter(Training::isHTTPProvider)
                .forEach(e -> {

                    final Map<String, String> providerProperties = e.providerProperties();

                    final String url = providerProperties.get(AlgorithmProvider.HTTP_PROVIDER_PREDICTION_URL_KEY);

                    LOGGER.log(Level.INFO, "Request {0}", url);

                    final Client client = ClientBuilder.newClient(AppConfig.getInstance().clientConfig());

                    final HashMap<String, Object> payload = new HashMap<>(2);
                    payload.put("localizationId", localization.id());
                    payload.put("samples", samplesList);

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

                                    final Position position = positionRepository.findById(positionPredicated).get();

                                    final Prediction prediction = new Prediction(
                                            requestId,
                                            localization.id(),
                                            positionPredicated,
                                            position.getLabel(),
                                            accuracy,
                                            e.getAlgorithmProvider().getId());

                                    predictionRepository.save(prediction);
                                }
                            }
                        }
                    }

                });

        try {
            predictionRepository.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not close resources", e);
        }

        return true;
    }

}
