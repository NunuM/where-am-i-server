package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.LocalizationSpamReport;
import me.nunum.whereami.model.persistance.LocalizationSpamRepository;
import me.nunum.whereami.utils.AppConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

public class LocalizationSpamRepositoryJpa
        extends JpaRepository<LocalizationSpamReport, Long>
        implements LocalizationSpamRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }


    @Override
    public LocalizationSpamReport findOrCreateByLocalization(Localization localization) {

        try {

            EntityManagerFactory entityManagerFactory = super.entityManagerFactory();
            EntityManager entityManager = entityManagerFactory.createEntityManager();

            return (LocalizationSpamReport) entityManager
                    .createNamedQuery("LocalizationSpam.findByLocalizationId")
                    .setParameter("localizationId", localization.id())
                    .getSingleResult();

        } catch (NoResultException e) {

            return this.save(new LocalizationSpamReport(localization));
        }
    }
}
