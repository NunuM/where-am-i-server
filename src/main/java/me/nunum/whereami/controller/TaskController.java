package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Task;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityAccessException;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.TaskRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.TaskRepositoryJpa;
import me.nunum.whereami.model.request.UpdateTask;
import me.nunum.whereami.service.notification.NotifyService;

import java.security.Principal;
import java.util.Optional;

public class TaskController implements AutoCloseable {

    private final TaskRepository taskRepository;
    private final DeviceRepository deviceRepository;

    public TaskController() {
        this.taskRepository = new TaskRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
    }

    @Override
    public void close() throws Exception {
        this.taskRepository.close();
    }

    public DTO updateTask(Principal userPrincipal, Long taskId, UpdateTask request) {

        final Device device = this.deviceRepository.findOrPersist(userPrincipal);

        final Optional<Task> optionalTask = this.taskRepository.findById(taskId);

        if (!optionalTask.isPresent()) {
            throw new EntityNotFoundException(String.format("Task %d is not found", taskId));
        }

        Task task = optionalTask.get();

        if (!task.getTraining().getAlgorithmProvider().belongs(device)) {
            throw new ForbiddenEntityAccessException("Forbidden access");
        }

        if (request.isFinish()) {

            task.trainingFinish();

            final Localization localization = task.getTraining().getLocalization();

            localization.incrementTrainedModels();

            NotifyService.trainingFinished(localization, task);

        } else {
            task.setState(Task.STATE.FINISH_SINK);
        }

        task = this.taskRepository.save(task);

        return task.toDTO();
    }
}
