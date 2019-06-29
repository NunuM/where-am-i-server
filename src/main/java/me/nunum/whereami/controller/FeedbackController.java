package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Feedback;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.FeedbackRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.FeedbackRepositoryJpa;
import me.nunum.whereami.model.request.NewFeedbackRequest;
import me.nunum.whereami.service.notification.NotifyService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FeedbackController implements AutoCloseable {

    private final DeviceRepository deviceRepository;
    private final FeedbackRepository repository;

    public FeedbackController() {
        this.repository = new FeedbackRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
    }


    public DTO processFeedback(Principal principal, NewFeedbackRequest request) {

        final Device device = this.deviceRepository.findOrPersist(principal);

        final Feedback feedback = this.repository.save(request.build(device));

        NotifyService.newFeedback(feedback);

        return feedback.toDTO();
    }

    @Override
    public void close() throws Exception {
        this.repository.close();
    }

    public List<DTO> listAllFeedback() {

        List<Feedback> feedbacks = new ArrayList<>();

        this.repository.all().forEach(feedbacks::add);

        return feedbacks.stream().map(Feedback::toDTO).collect(Collectors.toList());
    }
}
