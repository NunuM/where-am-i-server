package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.LocalizationSpamReport;


public interface LocalizationSpamRepository
        extends Repository<LocalizationSpamReport, Long> {

    LocalizationSpamReport findOrCreateByLocalization(Localization localization);
}
