package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Algorithm;
import me.nunum.whereami.model.persistance.AlgorithmRepository;
import me.nunum.whereami.utils.AppConfig;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public class AlgorithmRepositoryJpa
        extends JpaRepository<Algorithm, Long>
        implements AlgorithmRepository {

    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }

    @Override
    public List<Algorithm> paginate(Optional<Integer> page) {

        final Integer currentPage = page.map(p -> {
            if (p < 1) {
                return 1;
            } else return p;
        }).orElse(1);


        final CriteriaBuilder criteriaBuilder = super.entityManager().getCriteriaBuilder();

        CriteriaQuery<Algorithm> builderQuery = criteriaBuilder.createQuery(Algorithm.class);

        final Root<Algorithm> algorithmRoot = builderQuery.from(Algorithm.class);

        final CriteriaQuery<Algorithm> where = builderQuery
                .where(criteriaBuilder.equal(algorithmRoot.get("isApproved"), true))
                .orderBy(criteriaBuilder.desc(algorithmRoot.get("created")));

        return this.pageWithFiltering(where, currentPage);
    }

    @Override
    public Optional<Algorithm> findFirst() {
        return this.first();
    }
}
