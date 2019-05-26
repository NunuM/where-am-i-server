package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Algorithm;
import me.nunum.whereami.model.AlgorithmProvider;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Training;
import me.nunum.whereami.model.persistance.TrainingRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.RollbackException;
import java.util.List;
import java.util.Optional;

public class TrainingRepositoryJpa
        extends JpaRepository<Training, Long> implements TrainingRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }

    /**
     * {@inheritDoc}
     *
     * @param localization See {@link Localization}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Training> findByLocalization(Localization localization) {
        EntityManager entityManager = this.entityManager();
        return (List<Training>) entityManager.createNamedQuery("Training.findAllByLocalization").setParameter("localizationId", localization.id()).getResultList();
    }


    /**
     * {@inheritDoc}
     *
     * @param provider See {@link AlgorithmProvider}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Training> findAllTrainingWithProvider(AlgorithmProvider provider) {
        EntityManager entityManager = this.entityManager();
        return (List<Training>) entityManager.createNamedQuery("Training.findAllByProviderId").setParameter("providerId", provider.getId()).getResultList();
    }


    /**
     * {@inheritDoc}
     *
     * @param localization See {@link Localization}
     * @param algorithm    See {@link Algorithm}
     * @param provider     See {@link AlgorithmProvider}
     */
    @Override
    public Optional<Training> findTrainingByLocalizationAlgorithmAndProviderId(Localization localization, Algorithm algorithm, AlgorithmProvider provider) {
        EntityManager entityManager = this.entityManager();

        return Optional.ofNullable((Training) entityManager.createNamedQuery("Training.findTrainingByProviderAndAlgorithmId")
                .setParameter("algorithmId", algorithm.getId())
                .setParameter("algorithmProviderId", provider.getId())
                .setParameter("localizationId", localization.id())
                .getSingleResult());
    }


    /**
     * {@inheritDoc}
     *
     * @param provider See {@link AlgorithmProvider}
     */
    @Override
    public int deleteTrainingsAffectedBy(AlgorithmProvider provider) {
        int affected = 0;
        EntityManager entityManager = this.entityManager();

        try {
            entityManager.getTransaction().begin();
            affected = entityManager.createNamedQuery("Task.deleteAllByProviderId").setParameter("providerId", provider.getId()).executeUpdate();
            entityManager.createNamedQuery("Training.deleteAllByProviderId").setParameter("providerId", provider.getId()).executeUpdate();
            entityManager.getTransaction().commit();
        } catch (IllegalStateException | RollbackException e) {
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.close();
        }

        return affected;
    }
}
