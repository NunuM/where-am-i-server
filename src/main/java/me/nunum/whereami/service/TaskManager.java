package me.nunum.whereami.service;


import me.nunum.whereami.framework.domain.Executable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskManager implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(TaskManager.class.getSimpleName());

    private final ExecutorService executor;

    private final PriorityBlockingQueue<Executable> tasks;

    private static TaskManager ourInstance = new TaskManager();

    private TaskManager() {
        this.tasks = new PriorityBlockingQueue<>();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public synchronized void queue(Executable task) {
        this.tasks.add(task);
    }

    @Override
    public void run() {
        LOGGER.info("Started TaskManager");
        for (; ; ) {
            try {
                LOGGER.info("Waiting for task");
                final Executable task = this.tasks.take();
                LOGGER.info("Executing task");
                this.executor.submit(task).get();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error while executing task", e);
            }
        }
    }

    public static TaskManager getInstance() {
        return ourInstance;
    }
}
