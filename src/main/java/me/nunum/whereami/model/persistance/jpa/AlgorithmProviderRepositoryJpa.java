package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.AlgorithmProvider;
import me.nunum.whereami.model.persistance.AlgorithmProviderRepository;
import me.nunum.whereami.utils.AppConfig;

public class AlgorithmProviderRepositoryJpa
        extends JpaRepository<AlgorithmProvider, Long>
        implements AlgorithmProviderRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }
}
