package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.security.Principal;

public class DeviceRepositoryJpa
        extends JpaRepository<Device, Long>
        implements DeviceRepository {


    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }

    @Override
    public Device findOrPersist(Principal principal) {

        try {

            EntityManagerFactory entityManagerFactory = super.entityManagerFactory();
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            return (Device) entityManager.createNamedQuery("Device.findByInstance").setParameter("instance", principal.getName()).getSingleResult();

        } catch (NoResultException e) {

            return this.save(new Device(principal.getName()));
        }
    }
}
