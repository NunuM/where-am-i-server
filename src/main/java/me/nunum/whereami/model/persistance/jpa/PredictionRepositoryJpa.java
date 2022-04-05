package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Prediction;
import me.nunum.whereami.model.persistance.PredictionRepository;
import me.nunum.whereami.utils.AppConfig;

import jakarta.persistence.EntityManager;
import java.util.Date;
import java.util.List;

public class PredictionRepositoryJpa
        extends JpaRepository<Prediction, Long>
        implements PredictionRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }

    @Override
    public synchronized Long maxRequestIdForLocalization(Localization localization) {

        final EntityManager manager = entityManager();

        Long maximum = (Long) manager.createNamedQuery("Prediction.maxRequestId")
                .setParameter("localizationId", localization.id())
                .getSingleResult();

        if (maximum == null) {
            return 0L;
        }

        return maximum;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> allPredictionsSince(Device device, Localization localization, Date since) {
        final EntityManager manager = entityManager();

        return manager.createNamedQuery("Prediction.allPredictionsSince")
                .setParameter("localizationId", localization.id())
                .setParameter("since", since)
                .setParameter("deviceId", device.getId())
                .getResultList();
    }
}
