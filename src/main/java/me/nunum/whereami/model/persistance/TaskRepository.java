package me.nunum.whereami.model.persistance;

import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.model.Task;

import java.util.Iterator;
import java.util.stream.Stream;

public interface TaskRepository extends Repository<Task, Long>, AutoCloseable {

    Stream<Task> openTasks();

}
