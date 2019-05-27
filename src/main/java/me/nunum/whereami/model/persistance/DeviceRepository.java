package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Device;

import java.security.Principal;
import java.util.Set;

public interface DeviceRepository extends Repository<Device, Long>, AutoCloseable {

    /**
     * @param principal See {@link Principal}
     * @return See {@link Device}
     */
    Device findOrPersist(Principal principal);


    Set<Device> findAllDevicesInRole(String role);
}
