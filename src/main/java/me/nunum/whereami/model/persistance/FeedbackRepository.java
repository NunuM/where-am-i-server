package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Feedback;

public interface FeedbackRepository extends Repository<Feedback, Long>,
    AutoCloseable{
}
