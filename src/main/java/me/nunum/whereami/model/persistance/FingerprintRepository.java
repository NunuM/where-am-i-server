package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Fingerprint;
import me.nunum.whereami.model.Position;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public interface FingerprintRepository
        extends Repository<Fingerprint,Long>, AutoCloseable {

    boolean bulkFingerprints(List<Fingerprint> fingerprints);


    boolean deleteByPosition(Position position);


    List<Fingerprint> fingerprintByLocalizationIdAndWithIdGreater(Long localizationId, Long id, int page);
}
