package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.*;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityAccessException;
import me.nunum.whereami.model.persistance.*;
import me.nunum.whereami.model.persistance.jpa.*;
import me.nunum.whereami.model.request.NewTrainingRequest;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TrainingController implements AutoCloseable {

    private final TrainingRepository repository;
    private final DeviceRepository deviceRepository;
    private final AlgorithmRepository algorithmRepository;
    private final LocalizationRepository localizationRepository;
    private final TaskRepository taskRepository;

    public TrainingController() {
        this.repository = new TrainingRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
        this.algorithmRepository = new AlgorithmRepositoryJpa();
        this.localizationRepository = new LocalizationRepositoryJpa();
        this.taskRepository = new TaskRepositoryJpa();
    }


    public DTO submitTrainingRequest(Principal principal, NewTrainingRequest request, Localization localization) {

        final Device requesterDevice = deviceRepository.findOrPersist(principal);

        final Optional<Algorithm> algorithmOptional = algorithmRepository.findFirst();

        if (!algorithmOptional.isPresent()) {
            throw new EntityNotFoundException(String
                    .format("Algorithm %d, requested by %s, was not found", request.getAlgorithmId(), principal.getName())
            );
        }

        final Algorithm algorithm = algorithmOptional.get();

        final Optional<AlgorithmProvider> algorithmProvider = algorithm.algorithmProviderById(request.getProvider());

        if (!algorithmProvider.isPresent()) {
            throw new EntityNotFoundException(String
                    .format("Algorithm Provider %d, requested by %s, was not found", request.getProvider(), principal.getName())
            );
        }

        Training training = new Training(algorithm, algorithmProvider.get(), localization, requesterDevice);

        localization.addTraining(training);

        training = this.repository.save(training);

        this.taskRepository.save(new Task(0L, training));

        return training.toDTO();
    }

    public DTO trainingStatus(Principal userPrincipal, Long it) {

        final Optional<Training> someTraining = this.repository.findById(it);

        if (!someTraining.isPresent()) {
            throw new EntityNotFoundException(
                    String.format("Training status %d requested by %s, was not found",
                            it,
                            userPrincipal.getName())
            );
        }

        final Training training = someTraining.get();

        if (!training.
                isAllowedToCheckTheStatus(deviceRepository.findOrPersist(userPrincipal))) {

            throw new ForbiddenEntityAccessException(String.format("Requester %s not has permissions", userPrincipal.getName()));

        }

        return training.toDTO();
    }


    public List<DTO> allTrainingStatus(final Principal user, final Localization localization) {

        List<Training> trainingList = this.repository.findByLocalization(localization);

        return trainingList.stream().map(Training::toDTO).collect(Collectors.toList());
    }

    public DTO deleteTraining(final Principal userPrincipal, final Long trainingId) {

        final Device requester = this.deviceRepository.findOrPersist(userPrincipal);

        Optional<Training> optionalTraining = this.repository.findById(trainingId);

        if (optionalTraining.isPresent()) {

            final Training training = optionalTraining.get();

            if (training.isAllowedToCheckTheStatus(requester)) {

                DTO toSend = training.toDTO();
                this.repository.delete(training);

                return toSend;
            }

            throw new ForbiddenEntityAccessException(String
                    .format("Device %s request to delete training %d, however the ownership failed", requester.instanceId(), trainingId));
        }

        throw new EntityNotFoundException(String.format("Training %d was not found requested by %s", trainingId, requester.instanceId()));
    }

    @Override
    public void close() throws Exception {
        this.repository.close();
    }
}
