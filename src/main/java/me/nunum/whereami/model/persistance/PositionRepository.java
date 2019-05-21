package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;

import java.util.List;

public interface PositionRepository extends Repository<Position, Long>, AutoCloseable {


    List<Position> positionsByLocalization(Localization localization);

    boolean updateMetaData(Position position);
}
