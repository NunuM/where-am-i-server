package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Fingerprint;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.persistance.FingerprintRepository;
import me.nunum.whereami.model.request.FingerprintSample;
import me.nunum.whereami.utils.AppConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

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

    @SuppressWarnings("unchecked")
    @Override
    public Long predictUserLocalization(List<FingerprintSample> samples, Long localizationId) {

        long positionId = 0;

        if (!samples.isEmpty()) {


            final Set<String> ssids = samples.stream().map(FingerprintSample::getSsid).collect(Collectors.toSet());

            final String ssidsAsString = ssids.stream().reduce("", (acc, e) -> acc + ",'" + e + "'").replaceFirst(",", "");

            final EntityManager entityManager = entityManager();

            final List<Object[]> resultList =
                    entityManager
                            .createQuery("SELECT l.ssid,l.positionId,AVG (l.levelDBM) FROM Fingerprint l WHERE l.localizationId = " + localizationId + "  AND l.ssid IN (" + ssidsAsString + ") GROUP BY l.ssid, l.positionId")
                            .getResultList();

            if (!resultList.isEmpty()) {

                final HashMap<String, Object[]> hashMap = new HashMap<>(resultList.size());

                resultList.forEach(o -> hashMap.put(o[0].toString(), o));

                int nearestGap = 0;

                for (FingerprintSample s : samples) {
                    Object[] current = hashMap.get(s.getSsid());

                    if (current == null) {
                        continue;
                    }

                    int dbLdbm = (int) (double) current[2];
                    int d = s.getLevelDBM() + dbLdbm;
                    if (nearestGap == 0 || nearestGap < d) {
                        nearestGap = d;
                        positionId = (long) current[1];
                    }
                }
            }

        }

        return positionId;
    }
}
