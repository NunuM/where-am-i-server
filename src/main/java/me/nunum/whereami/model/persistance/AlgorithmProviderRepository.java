package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.AlgorithmProvider;

import java.util.Optional;

public interface AlgorithmProviderRepository
 extends Repository<AlgorithmProvider, Long>, AutoCloseable {

    Optional<String> algorithmByProvider(AlgorithmProvider algorithmProvider);

}
