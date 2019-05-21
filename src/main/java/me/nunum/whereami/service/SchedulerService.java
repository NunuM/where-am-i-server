package me.nunum.whereami.service;


import me.nunum.whereami.model.Task;
import me.nunum.whereami.model.persistance.TaskRepository;
import me.nunum.whereami.model.persistance.jpa.TaskRepositoryJpa;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SchedulerService implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(SchedulerService.class.getSimpleName());

    @Override
    public void run() {

        int pageCounter = 0;

        final TaskRepository tasks = new TaskRepositoryJpa();

        LOGGER.log(Level.INFO, "Starting SchedulerService service");

        ExecutorService service = Executors.newSingleThreadExecutor();

        Iterator<Task> openTasks = tasks.openTasks(pageCounter);

        while (!openTasks.hasNext()) {

            try {

                CompletableFuture.runAsync(() -> {

                    Task next = openTasks.next();

                    Long cursor = next.getCursor();


                }, service).get();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
