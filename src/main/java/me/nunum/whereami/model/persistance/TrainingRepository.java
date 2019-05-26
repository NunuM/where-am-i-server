package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Algorithm;
import me.nunum.whereami.model.AlgorithmProvider;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository
        extends Repository<Training, Long>, AutoCloseable {


    /**
     * Obtain a list of trainings by a given a localization
     *
     * @param localization See {@link Localization}
     * @return List of Training
     */
    List<Training> findByLocalization(Localization localization);

    /**
     * Obtain a list of trainings by a given provider
     *
     * @param provider See {@link AlgorithmProvider}
     * @return List of Training
     */
    List<Training> findAllTrainingWithProvider(AlgorithmProvider provider);

    /**
     * Obtain training for their localization.
     * <p>
     * Cannot exists more than one training for
     * the same localization that have the same
     * algorithm and same provider.
     *
     * @param localization See {@link Localization}
     * @param algorithm    See {@link Algorithm}
     * @param provider     See {@link AlgorithmProvider}
     * @return Nullable Training
     * @throws me.nunum.whereami.model.exceptions.EntityAlreadyExists
     */
    Optional<Training> findTrainingByLocalizationAlgorithmAndProviderId(Localization localization, Algorithm algorithm, AlgorithmProvider provider);


    /**
     * Delete all rows that have this provider
     *
     * @param provider Algorithm Provider
     * @return number of affected rows
     */
    int deleteTrainingsAffectedBy(AlgorithmProvider provider);
}
