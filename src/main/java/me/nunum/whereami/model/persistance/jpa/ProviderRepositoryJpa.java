package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Provider;
import me.nunum.whereami.model.persistance.ProviderRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import java.util.Optional;

public class ProviderRepositoryJpa
        extends JpaRepository<Provider, Long>
        implements ProviderRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }

    @Override
    public Optional<Provider> findByToken(String token) {
        final EntityManager entityManager = entityManager();

        return Optional.ofNullable((Provider) entityManager.createNamedQuery("Provider.findByToken")
                .setParameter("token", token)
                .getSingleResult());

    }
}
