package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.exceptions.EntityAlreadyExists;
import me.nunum.whereami.model.exceptions.NotAbleToPersistDeviceException;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

public class DeviceRepositoryJpa
        extends JpaRepository<Device, Long>
        implements DeviceRepository {


    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }

    @Override
    public Device findOrPersist(Principal principal) {

        int retry = 0;
        Device device = null;

        do {
            try {

                EntityManagerFactory entityManagerFactory = super.entityManagerFactory();
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                device = (Device) entityManager.createNamedQuery("Device.findByInstance").setParameter("instance", principal.getName()).getSingleResult();

            } catch (NoResultException e) {
                try {
                    device = this.save(new Device(principal.getName()));
                } catch (EntityAlreadyExists e1) {
                    //continue
                }
            }

            retry++;
        } while (retry < 3 && device == null);

        if (device == null) {
            throw new NotAbleToPersistDeviceException("The device with id " + principal.getName() + ", was not persisted due data racing.");
        }

        return device;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Device> findAllDevicesInRole(String role) {
        final EntityManager entityManager = entityManager();
        return (Set<Device>) entityManager.createNamedQuery("Device.findAllDevicesInRole")
                .setParameter("role", role)
                .getResultList()
                .stream()
                .collect(Collectors.toSet());
    }
}
