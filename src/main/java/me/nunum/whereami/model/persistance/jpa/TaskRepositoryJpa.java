package me.nunum.whereami.model.persistance.jpa;

import me.nunum.whereami.framework.persistence.repositories.impl.jpa.JpaRepository;
import me.nunum.whereami.model.Task;
import me.nunum.whereami.model.Training;
import me.nunum.whereami.model.persistance.TaskRepository;
import me.nunum.whereami.utils.AppConfig;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.stream.Stream;

public class TaskRepositoryJpa
        extends JpaRepository<Task, Long>
        implements TaskRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String persistenceUnitName() {
        return AppConfig.JPA_UNIT;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Stream<Task> openTasks() {

        final EntityManager manager = entityManager();

        return manager.createNamedQuery("Task.allByStatus")
                .setParameter("st", Task.STATE.RUNNING)
                .getResultStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Task> findTaskByTrainingId(Training training) {
        final EntityManager entityManager = entityManager();
        return Optional
                .ofNullable((Task) entityManager
                        .createNamedQuery("Task.findTaskByTrainingId")
                        .setParameter("trainingId", training.getId())
                        .getSingleResult());
    }
}
