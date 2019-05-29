package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.*;
import me.nunum.whereami.model.exceptions.EntityAlreadyExists;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityAccessException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityCreationException;
import me.nunum.whereami.model.persistance.AlgorithmRepository;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.TaskRepository;
import me.nunum.whereami.model.persistance.TrainingRepository;
import me.nunum.whereami.model.persistance.jpa.AlgorithmRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.TaskRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.TrainingRepositoryJpa;
import me.nunum.whereami.model.request.NewTrainingRequest;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class TrainingController implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(TrainingController.class.getSimpleName());

    private final TrainingRepository repository;
    private final DeviceRepository deviceRepository;
    private final AlgorithmRepository algorithmRepository;
    private final TaskRepository taskRepository;

    public TrainingController() {
        this.repository = new TrainingRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
        this.algorithmRepository = new AlgorithmRepositoryJpa();
        this.taskRepository = new TaskRepositoryJpa();
    }


    /**
     * Register new training for given localization
     *
     * @param principal    Device
     * @param request      Information to create a new instance of Training
     * @param localization Localization associated. See {@link Localization}
     * @return See {@link me.nunum.whereami.model.dto.TrainingDTO}
     * @throws EntityNotFoundException
     * @throws ForbiddenEntityAccessException
     */
    public DTO submitTrainingRequest(Principal principal, NewTrainingRequest request, Localization localization) {

        final Device requesterDevice = deviceRepository.findOrPersist(principal);

        final Optional<Algorithm> algorithmOptional = algorithmRepository.findById(request.getAlgorithmId());

        if (!algorithmOptional.isPresent()) {
            throw new EntityNotFoundException(String
                    .format("Algorithm %d, requested by %s, was not found", request.getAlgorithmId(), principal.getName())
            );
        }

        final Algorithm algorithm = algorithmOptional.get();

        if (!algorithm.isApproved()) {
            throw new ForbiddenEntityCreationException(String.format("Algorithm %d is not approved for use.", request.getAlgorithmId()));
        }

        if (!localization.isOwner(requesterDevice)) {
            throw new ForbiddenEntityAccessException(String.format("Requester %s is not allowed to request training on this localization %d.", requesterDevice.instanceId(), localization.id()));
        }

        final Optional<AlgorithmProvider> algorithmProvider = algorithm.algorithmProviderById(request.getProviderId());

        if (!algorithmProvider.isPresent()) {
            throw new EntityNotFoundException(String
                    .format("Algorithm Provider %d, requested by %s, was not found", request.getProviderId(), principal.getName())
            );
        }

        final AlgorithmProvider provider = algorithmProvider.get();

        if (!provider.wasVerified()) {
            throw new ForbiddenEntityCreationException(String.format("Provider %s for this algorithm %d is not yet verified the account.", request.getProviderId(), request.getAlgorithmId()));
        }

        try {
            Training training = new Training(algorithm, provider, localization);

            localization.addTraining(training);

            training = this.repository.save(training);

            return training.toDTO();

        } catch (EntityAlreadyExists e) {

            try {
                this.close();
            } catch (Exception e1) {
                LOGGER.log(Level.FINE, "Could not close previous entity manager", e1);
            }

            LOGGER.log(Level.INFO, String
                    .format("Training for algorithm %d and provider %d for localization %d already exists, reset task",
                            algorithm.getId(),
                            provider.getId(),
                            localization.id()));

            final Optional<Training> training = this.repository.findTrainingByLocalizationAlgorithmAndProviderId(localization, algorithm, provider);

            if (!training.isPresent()) {
                LOGGER.log(Level.SEVERE, "Database gives entity already exists, however the second query has failed");
                throw new EntityNotFoundException("Training not found");
            }

            final Training training1 = training.get();

            final Optional<Task> task = this.taskRepository.findTaskByTrainingId(training1);

            task.ifPresent(t -> {
                t.setState(Task.STATE.RUNNING);
                this.taskRepository.save(t);
            });

            return training1.toDTO();
        }
    }


    /**
     * Display the status for given training Id
     *
     * @param userPrincipal See {@link Device}
     * @param it            Training Id
     * @return See {@link me.nunum.whereami.model.dto.TrainingDTO}
     */
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


    /**
     * Display a list of training associated by their localization
     *
     * @param user         See {@link Device}
     * @param localization See {@link Localization}
     * @return List of {@link me.nunum.whereami.model.dto.TrainingDTO}
     */
    public List<DTO> allTrainingStatus(final Principal user, final Localization localization) {

        List<Training> trainingList = this.repository.findByLocalization(localization);

        return trainingList.stream().map(Training::toDTO).collect(Collectors.toList());
    }


    /**
     * Delete training given their id
     *
     * @param userPrincipal Device
     * @param trainingId    Training Id
     * @return See {@link me.nunum.whereami.model.dto.TrainingDTO}
     */
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


    /**
     * Is never trowed, otherwise the client would get a 500 instead of the actual processed response
     *
     * @throws Exception It must never occur
     */
    @Override
    public void close() throws Exception {
        this.repository.close();
    }
}
