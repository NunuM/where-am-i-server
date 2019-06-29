package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Feedback;
import me.nunum.whereami.model.persistance.FeedbackRepository;
import me.nunum.whereami.utils.AppConfig;

public class FeedbackRepositoryJpa
        extends JpaRepository<Feedback, Long>
        implements FeedbackRepository {
    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }
}
