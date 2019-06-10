package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Prediction;

import java.util.Date;
import java.util.List;

public interface PredictionRepository
        extends Repository<Prediction, Long> {

    Long maxRequestIdForLocalization(Localization localization);


    List<Prediction> allPredictionsSince(Localization localization, Date since);

}
