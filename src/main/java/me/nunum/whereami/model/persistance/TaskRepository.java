package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Task;
import me.nunum.whereami.model.Training;

import java.util.Optional;
import java.util.stream.Stream;

public interface TaskRepository extends Repository<Task, Long>, AutoCloseable {

    /**
     * Obtain a list of running tasks
     *
     * @return Streaming
     */
    Stream<Task> openTasks();


    /**
     * Obtain task given a training
     *
     * @param training
     * @return Nullable task
     */
    Optional<Task> findTaskByTrainingId(Training training);

}
