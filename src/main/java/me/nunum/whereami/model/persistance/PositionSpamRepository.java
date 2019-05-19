package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.PositionSpamReport;

public interface PositionSpamRepository extends Repository<PositionSpamReport, Long> {

    PositionSpamReport findOrCreateByPosition(Position position);
}
