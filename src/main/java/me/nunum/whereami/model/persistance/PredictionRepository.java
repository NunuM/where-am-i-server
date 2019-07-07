package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Prediction;

import java.util.Date;
import java.util.List;

public interface PredictionRepository
        extends Repository<Prediction, Long>, AutoCloseable {

    Long maxRequestIdForLocalization(Localization localization);


    List<Prediction> allPredictionsSince(Device device, Localization localization, Date since);

}
