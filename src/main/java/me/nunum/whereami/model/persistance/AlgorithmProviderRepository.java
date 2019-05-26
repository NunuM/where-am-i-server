package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.AlgorithmProvider;

public interface AlgorithmProviderRepository
 extends Repository<AlgorithmProvider, Long>, AutoCloseable {
}
