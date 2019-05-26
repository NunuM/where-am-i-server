package me.nunum.whereami.migration;


import me.nunum.whereami.model.Role;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Migration1 implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Migration1.class.getSimpleName());

    @Override
    public void run() {
        try {
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(AppConfig.JPA_UNIT);
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(new Role("admin"));
            entityManager.persist(new Role("provider"));
            entityManager.getTransaction().commit();
            entityManager.close();

        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Error on running migration1", e);
        }
    }

    public static void main(String[] args) {
        new Migration1().run();
    }
}
