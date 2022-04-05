package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Provider;
import me.nunum.whereami.model.persistance.ProviderRepository;
import me.nunum.whereami.utils.AppConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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

        try {
            return Optional.ofNullable((Provider) entityManager.createNamedQuery("Provider.findByToken")
                    .setParameter("token", token)
                    .getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Provider> findByDevice(Device device) {
        final EntityManager entityManager = entityManager();

        try {
            return Optional.ofNullable((Provider) entityManager.createNamedQuery("Provider.findByDevice")
                    .setParameter("deviceId", device.getId())
                    .getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
