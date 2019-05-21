package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Algorithm;

import java.util.List;
import java.util.Optional;

public interface AlgorithmRepository extends Repository<Algorithm, Long>, AutoCloseable {

    List<Algorithm> paginate(Optional<Integer> page);
    
    Optional<Algorithm> findFirst();
}
