package me.nunum.whereami.migration;


import me.nunum.whereami.model.*;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Migration1 implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Migration1.class.getSimpleName());

    @Override
    public synchronized void run() {
        LOGGER.info("Migration started");
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(AppConfig.JPA_UNIT, AppConfig.persistenceUnitOverrideConfigs());
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {

            String adminInstallation = UUID.randomUUID().toString();
            LOGGER.log(Level.INFO, "Admin installation ID {0}", adminInstallation);
            Device device = new Device(adminInstallation);

            entityManager.persist(device);

            Role admin = new Role("admin");
            admin.addDevice(device);
            entityManager.persist(admin);

            Role provider = new Role("provider");
            provider.addDevice(device);
            entityManager.persist(provider);

            Algorithm algorithm = new Algorithm("Mean", "Nuno", "https://en.wikipedia.org/wiki/Mean", true, device);
            entityManager.persist(algorithm);


            Provider aProvider = new Provider("nuno@nunum.me", UUID.randomUUID().toString(), true, device);
            entityManager.persist(aProvider);

            final HashMap<String, String> map = new HashMap<>(3);
            map.put(AlgorithmProvider.HTTP_PROVIDER_INGESTION_URL_KEY, "http://www.mocky.io/v2/5cfd86b93200007100ccd52f");
            map.put(AlgorithmProvider.HTTP_PROVIDER_PREDICTION_URL_KEY, String.format("/api/algorithm/%d/implementation/1", algorithm.getId()));
            algorithm.addProvider(new AlgorithmProvider(aProvider, AlgorithmProvider.METHOD.HTTP, map));


            final Post wikiPost = new Post("Wi-Fi positioning system", "https://whereami.nunum.me/img/w.png", "https://en.wikipedia.org/wiki/Wi-Fi_positioning_system");
            final Post myPost = new Post("Indoor Positioning Systems using Wi-Fi", "https://whereami.nunum.me/img/s.png", "https://whereami.nunum.me/pdf/paper_1140460_1140358.pdf");

            entityManager.persist(wikiPost);
            entityManager.persist(myPost);

            entityManager.flush();
            transaction.commit();

        } catch (Throwable e) {
            if (transaction.isActive())
                transaction.rollback();
            LOGGER.log(Level.SEVERE, "Error on running migration 1", e);
        } finally {
            entityManager.close();
        }

        LOGGER.info("Migration finished");
    }
}
