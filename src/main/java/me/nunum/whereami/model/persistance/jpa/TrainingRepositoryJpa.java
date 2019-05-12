package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Training;
import me.nunum.whereami.model.persistance.TrainingRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class TrainingRepositoryJpa
        extends JpaRepository<Training, Long> implements TrainingRepository {
    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }

    @Override
    public List<Training> findByLocalization(Localization localization) {
        EntityManagerFactory entityManagerFactory = super.entityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return (List<Training>) entityManager.createNamedQuery("Training.findAllByLocalization").setParameter("localizationId", localization.id()).getResultList();
    }
}
