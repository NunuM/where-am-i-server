package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Algorithm;
import me.nunum.whereami.model.persistance.AlgorithmRepository;
import me.nunum.whereami.utils.AppConfig;

import java.util.ArrayList;
import java.util.Iterator;
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

        List<Algorithm> algorithms = new ArrayList<>();

        final Integer currentPage = page.map(p -> {
            if (p < 1) {
                return 1;
            } else return p;
        }).orElse(1);

        final Iterator<Algorithm> theAlgorithmIteratorIterator = this.iterator(currentPage);

        theAlgorithmIteratorIterator.forEachRemaining(algorithms::add);

        return algorithms;
    }

    @Override
    public Optional<Algorithm> findFirst() {
        return this.first();
    }
}
