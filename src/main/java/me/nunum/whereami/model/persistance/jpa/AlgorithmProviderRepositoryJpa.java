package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.AlgorithmProvider;
import me.nunum.whereami.model.persistance.AlgorithmProviderRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import java.util.Optional;

public class AlgorithmProviderRepositoryJpa
        extends JpaRepository<AlgorithmProvider, Long>
        implements AlgorithmProviderRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }

    @Override
    public Optional<String> algorithmByProvider(AlgorithmProvider algorithmProvider) {

        final EntityManager entityManager = entityManager();

        String algorithmName = (String) entityManager
                .createNamedQuery("Algorithm.algorithmNameByProvider")
                .setParameter("provider", algorithmProvider)
                .getSingleResult();

        return Optional.ofNullable(algorithmName);
    }
}
