package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Localization;

import java.util.List;
import java.util.Optional;

public interface LocalizationRepository extends Repository<Localization, Long> {

    List<Localization> paginate(Optional<Integer> page);

    List<Localization> searchWithPagination(Optional<Integer> page, Optional<String> localizationName);
}
