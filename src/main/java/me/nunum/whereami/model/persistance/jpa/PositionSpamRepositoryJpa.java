package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.LocalizationSpamReport;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.PositionSpamReport;
import me.nunum.whereami.model.persistance.PositionSpamRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

public class PositionSpamRepositoryJpa
        extends JpaRepository<PositionSpamReport, Long>
        implements PositionSpamRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }


    @Override
    public PositionSpamReport findOrCreateByPosition(Position position) {

        try {

            EntityManagerFactory entityManagerFactory = super.entityManagerFactory();
            EntityManager entityManager = entityManagerFactory.createEntityManager();

            return (PositionSpamReport) entityManager
                    .createNamedQuery("PositionSpam.findByPositionId")
                    .setParameter("positionId", position.id())
                    .getSingleResult();

        } catch (NoResultException e) {

            return this.save(new PositionSpamReport(position));
        }
    }
}
