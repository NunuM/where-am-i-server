package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Training;

import java.util.List;

public interface TrainingRepository
        extends Repository<Training, Long> {

    List<Training> findByLocalization(Localization localization);

}
