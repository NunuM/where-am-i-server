package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Device;

import java.security.Principal;

public interface DeviceRepository extends Repository<Device, Long> {

    Device findOrPersist(Principal principal);

}
