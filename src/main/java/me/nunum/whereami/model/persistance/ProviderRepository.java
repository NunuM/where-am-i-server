package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Provider;

import java.util.Optional;

public interface ProviderRepository extends Repository<Provider, Long> {
    Optional<Provider> findByToken(String token);

    Optional<Provider> findByDevice(Device device);

}
