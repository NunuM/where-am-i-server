package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Fingerprint;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.persistance.FingerprintRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.stream.Stream;

public class FingerprintRepositoryJpa
        extends JpaRepository<Fingerprint, Long>
        implements FingerprintRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }

    @Override
    public boolean bulkFingerprints(List<Fingerprint> fingerprints) {

        if (super.entityManager() == null) {
            throw new IllegalArgumentException();
        }

        EntityManager em = entityManager();

        try {

            em.getTransaction();
            em.getTransaction().begin();

            for (int i = 0; i < fingerprints.size(); i++) {
                em.persist(fingerprints.get(i));
                if ((i % 20) == 0) {
                    em.flush();
                    em.clear();
                }
            }

            em.flush();
            em.clear();

            em.getTransaction().commit();

        } finally {
            em.close();
        }

        return true;
    }

    @Override
    public boolean deleteByPosition(Position position) {

        final EntityManager em = entityManager();
        final EntityTransaction transaction = em.getTransaction();

        try {

            transaction.begin();

            em.createNativeQuery("DELETE FROM fingerprint WHERE positionid=?")
                    .setParameter(1, position.id())
                    .executeUpdate();

            transaction.commit();

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Not able to delete fingerprints", e);

            transaction.rollback();

            return false;
        }

        return true;
    }

    @Override
    public List<Fingerprint> fingerprintByLocalizationIdAndWithIdGreater(Long localizationId, Long id, int batchSize) {

        final CriteriaBuilder criteriaBuilder = super.entityManager().getCriteriaBuilder();

        CriteriaQuery<Fingerprint> builderQuery = criteriaBuilder.createQuery(Fingerprint.class);

        final Root<Fingerprint> fingerprintRoot = builderQuery.from(Fingerprint.class);

        CriteriaQuery<Fingerprint> where = builderQuery
                .where(criteriaBuilder.equal(fingerprintRoot.get("localizationId"), localizationId))
                .where(criteriaBuilder.gt(fingerprintRoot.get("id"), id))
                .orderBy(criteriaBuilder.asc(fingerprintRoot.get("id")));

        return this.entityManager().createQuery(where).setMaxResults(batchSize).getResultList();
    }
}
