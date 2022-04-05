package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.persistance.PositionRepository;
import me.nunum.whereami.model.stats.NetworkingStats;
import me.nunum.whereami.model.stats.StrongRouterSignal;
import me.nunum.whereami.utils.AppConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class PositionRepositoryJpa
        extends JpaRepository<Position, Long>
        implements PositionRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<Position> positionsByLocalization(Localization localization) {

        try {

            EntityManagerFactory entityManagerFactory = super.entityManagerFactory();
            EntityManager entityManager = entityManagerFactory.createEntityManager();

            return (List<Position>) entityManager
                    .createNamedQuery("Position.findByLocalizationId")
                    .setParameter("localizationId", localization.id())
                    .getResultList();

        } catch (NoResultException e) {

            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean updateMetaData(Position position) {

        try {

            EntityManagerFactory entityManagerFactory = super.entityManagerFactory();
            EntityManager entityManager = entityManagerFactory.createEntityManager();


            final List<Object[]> resultList = entityManager
                    .createNativeQuery("SELECT AVG(leveldbm) AS levelDbm, ssid AS ssid FROM FINGERPRINT WHERE positionid=? GROUP BY ssid;")
                    .setParameter(1, position.id())
                    .getResultList();


            final Optional<StrongRouterSignal> strongRouter = resultList
                    .stream()
                    .map(e -> new StrongRouterSignal((int) e[0], (String) e[1]))
                    .max(Comparator.comparingInt(StrongRouterSignal::getLevelDvm));

            strongRouter.ifPresent(strongRouterSignal -> position.setStrongestRouter(strongRouterSignal.getSsid()));

            final List<Object[]> networkingStats = entityManager.createNativeQuery("SELECT COUNT(DISTINCT (ssid)) AS numberOfNetworks,bssid AS bssid FROM FINGERPRINT WHERE positionid=? GROUP BY bssid;")
                    .setParameter(1, position.id())
                    .getResultList();


            networkingStats.stream()
                    .map(e -> new NetworkingStats(((int) (long) e[0]), (String) e[1]))
                    .map(NetworkingStats::getNumberOfNetworks)
                    .reduce((e0, e1) -> e0 + e1).ifPresent(position::setNumberOfNetworks);

            position.setNumberOfRouters(networkingStats.size());


            this.save(position);

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Not able to update metadata", e);

            return false;
        }

        return true;
    }
}
