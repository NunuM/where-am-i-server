package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Algorithm;
import me.nunum.whereami.model.AlgorithmProvider;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Training;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityAccessException;
import me.nunum.whereami.model.persistance.AlgorithmProviderRepository;
import me.nunum.whereami.model.persistance.AlgorithmRepository;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.TrainingRepository;
import me.nunum.whereami.model.persistance.jpa.AlgorithmProviderRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.AlgorithmRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.TrainingRepositoryJpa;
import me.nunum.whereami.model.request.NewAlgorithmProvider;
import me.nunum.whereami.model.request.NewAlgorithmRequest;
import me.nunum.whereami.service.NotifyService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AlgorithmController implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(AlgorithmController.class.getSimpleName());

    private final AlgorithmRepository repository;
    private final DeviceRepository deviceRepository;
    private final AlgorithmProviderRepository providerRepository;

    /**
     * constructor
     */
    public AlgorithmController() {
        this.repository = new AlgorithmRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
        this.providerRepository = new AlgorithmProviderRepositoryJpa();
    }

    /**
     * Display a list of algorithm entities
     *
     * @param page Page used for paginate algorithm
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmDTO}
     */
    public List<DTO> algorithms(Optional<Integer> page) {

        return this.repository
                .paginate(page)
                .stream()
                .map(Algorithm::toDTO)
                .collect(Collectors.toList());

    }

    /**
     * Register new algorithm
     *
     * @param algorithmRequest New Algorithm Request
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmProviderDTO}
     */
    public DTO addNewAlgorithm(NewAlgorithmRequest algorithmRequest) {
        return this.repository.save(algorithmRequest.build()).toDTO();
    }

    /**
     * Display information for a specific algorithm
     *
     * @param aId Algorithm ID
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmProviderDTO}
     * @throws EntityNotFoundException Algorithm ID does not exists in database
     */
    public DTO algorithm(Long aId) {

        Optional<Algorithm> algorithmOptional = this.repository.findById(aId);

        if (algorithmOptional.isPresent()) {
            return algorithmOptional.get().toDTO();
        }

        throw new EntityNotFoundException(String.format("Algorithm with id:%d, was not found", aId));
    }

    /**
     * Register new algorithm provider
     *
     * @param aId               Algorithm ID
     * @param algorithmProvider Algorithm Provider Request
     * @param principal         Device
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmProviderDTO}
     * @throws EntityNotFoundException Algorithm ID does not exists in database
     */
    public DTO registerNewAlgorithmProvider(Long aId, NewAlgorithmProvider algorithmProvider, Principal principal) {

        final Device device = this.deviceRepository.findOrPersist(principal);

        Optional<Algorithm> algorithmOptional = this.repository.findById(aId);

        if (!algorithmOptional.isPresent()) {
            throw new EntityNotFoundException(String.format("Algorithm with id:%d, was not found", aId));
        }

        Algorithm algorithm = algorithmOptional.get();

        AlgorithmProvider provider = algorithmProvider.build(device);

        provider = this.providerRepository.save(provider);

        algorithm.addProvider(provider);

        this.repository.save(algorithm);

        return provider.toDTO();
    }

    /**
     * Deletes a algorithm provider.
     *
     * @param aId           Algorithm ID
     * @param pId           Algorithm Provider ID
     * @param userPrincipal Device
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmProviderDTO}
     * @throws EntityNotFoundException        Algorithm Provider ID does not exists in database
     * @throws ForbiddenEntityAccessException Device is not owner of the entity
     */
    public DTO deleteProvider(Long aId, Long pId, Principal userPrincipal) {

        Device device = this.deviceRepository.findOrPersist(userPrincipal);

        Optional<AlgorithmProvider> algorithmProvider = this.providerRepository.findById(pId);

        if (!algorithmProvider.isPresent()) {
            throw new EntityNotFoundException(String.format("Provider %s not found", pId));
        }

        final AlgorithmProvider provider = algorithmProvider.get();

        if (!provider.belongs(device)) {
            throw new ForbiddenEntityAccessException(String
                    .format("Device %s not has permissions to delete provider %d", userPrincipal.getName(), pId));
        }

        try {
            // Fast path, try to delete
            this.providerRepository.delete(provider);
            return provider.toDTO();
        } catch (Exception e) {
            LOGGER.log(Level.FINE, String.format("Fast path for provider %d deletion as fail. Calculate damage", pId), e);
        }

        try {
            /** Close previous transactions */
            this.providerRepository.close();
        } catch (Exception e) {
            LOGGER.log(Level.FINE, String.format("Could not close transactions in provider %d deletion process", pId), e);
        }

        /** Notify affected devices */
        final TrainingRepository trainingRepository = new TrainingRepositoryJpa();
        List<Training> trainings = trainingRepository.findAllTrainingWithProvider(provider);
        final Set<Device> devices = trainings.stream().map(e -> e.getLocalization().getOwner()).collect(Collectors.toSet());
        final NotifyService notifyService = NotifyService.providerDeletionNotification(devices);
        notifyService.run();

        /**  Delete provider */
        int affectedDevices = trainingRepository.deleteTrainingsAffectedBy(provider);

        LOGGER.log(Level.INFO, String.format("Provider %d deletion affected %d training", pId, affectedDevices));

        this.providerRepository.delete(provider);

        return provider.toDTO();
    }


    /**
     * Is never trowed, otherwise the client would get a 500 instead of the actual processed response
     *
     * @throws Exception It must never occur
     */
    @Override
    public void close() throws Exception {
        repository.close();
    }
}
