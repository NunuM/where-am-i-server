package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;

import java.util.List;
import java.util.Optional;

public interface LocalizationRepository extends Repository<Localization, Long>, AutoCloseable {

    void deleteLocalization(Localization localization);


    List<Localization> searchWithPagination(Device device,
                                            Optional<Integer> page,
                                            Optional<String> localizationName,
                                            Optional<String> trained);
}
