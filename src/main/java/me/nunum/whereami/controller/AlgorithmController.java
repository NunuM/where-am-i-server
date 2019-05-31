package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.*;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityAccessException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityCreationException;
import me.nunum.whereami.model.persistance.*;
import me.nunum.whereami.model.persistance.jpa.*;
import me.nunum.whereami.model.request.*;
import me.nunum.whereami.service.notification.NotifyService;

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
    private final AlgorithmProviderRepository algorithmProviderRepository;
    private final ProviderRepository providerRepository;
    private final TrainingRepository trainingRepository;

    /**
     * constructor
     */
    public AlgorithmController() {
        this.repository = new AlgorithmRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
        this.providerRepository = new ProviderRepositoryJpa();
        this.algorithmProviderRepository = new AlgorithmProviderRepositoryJpa();
        this.trainingRepository = new TrainingRepositoryJpa();
    }

    /**
     * Display a list of approved algorithm entities
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
     * Update algorithm instance
     *
     * @param userPrincipal See {@link Principal}
     * @param aId           Algorithm Id
     * @param request       See {@link UpdateAlgorithmRequest}
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmDTO}
     * @throws EntityNotFoundException
     */
    public DTO updateAlgorithm(Principal userPrincipal, Long aId, UpdateAlgorithmRequest request) {

        final Device device = this.deviceRepository.findOrPersist(userPrincipal);

        final Algorithm algorithm = getAlgorithm(aId);

        if (!algorithm.isPusblisher(device)) {
            throw new ForbiddenEntityAccessException(String.format("Device %s not have update privileges to update algorithm %d", device.instanceId(), aId));
        }

        return this.repository.save(request.upateAlgorithm(algorithm)).toDTO();
    }


    /**
     * Updates algorithm approval
     *
     * @param aId     Algorithm Id
     * @param request See {@link ApprovalRequest}
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmDTO}
     * @throws EntityNotFoundException
     */
    public DTO updateEntityApproval(Long aId, ApprovalRequest request) {

        final Algorithm algorithm = getAlgorithm(aId);

        final Algorithm toUpdate = request.updateAprroval(algorithm);

        return this.repository.save(toUpdate).toDTO();
    }


    /**
     * Deletes algorithm instance
     *
     * @param aId Algorithm Id
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmDTO}
     * @throws EntityNotFoundException
     */
    public DTO deleteAlgorithm(Long aId) {

        final Algorithm algorithm = getAlgorithm(aId);

        this.repository.delete(algorithm);

        return algorithm.toDTO();
    }


    /**
     * Helper method to obtain an algorithm
     *
     * @param aId Algorithm Id
     * @return Nullable {@link Algorithm}
     * @throws EntityNotFoundException
     */
    private Algorithm getAlgorithm(Long aId) {
        final Optional<Algorithm> algorithmOptional = this.repository.findById(aId);

        if (!algorithmOptional.isPresent()) {
            throw new EntityNotFoundException(String.format("Algorithm %s not found", aId));
        }

        return algorithmOptional.get();
    }


    /**
     * Register new algorithm
     *
     * @param algorithmRequest New Algorithm Request
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmProviderDTO}
     */
    public DTO addNewAlgorithm(Principal principal, NewAlgorithmRequest algorithmRequest) {

        final Device publisher = this.deviceRepository.findOrPersist(principal);

        final Algorithm algorithm = this.repository.save(algorithmRequest.build(publisher));

        try {
            Set<Device> devices = this.deviceRepository.findAllDevicesInRole(Role.ADMIN);
            NotifyService.newAlgorithmNotification(devices).run();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Notification was not sent", e);
        }

        return algorithm.toDTO();
    }

    /**
     * Display information for a specific algorithm
     *
     * @param aId Algorithm ID
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmProviderDTO}
     * @throws EntityNotFoundException Algorithm ID does not exists in database
     */
    public DTO algorithm(Long aId) {
        return this.getAlgorithm(aId).toDTO();
    }

    /**
     * Register new algorithm provider
     *
     * @param aId                      Algorithm ID
     * @param algorithmProviderRequest Algorithm Provider Request
     * @param principal                Device
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmProviderDTO}
     * @throws EntityNotFoundException Algorithm ID does not exists in database
     */
    public DTO registerNewAlgorithmProvider(Long aId, NewAlgorithmProvider algorithmProviderRequest, Principal principal) {

        final Device device = this.deviceRepository.findOrPersist(principal);

        Algorithm algorithm = this.getAlgorithm(aId);

        Optional<Provider> optionalProvider = this.providerRepository.findByDevice(device);

        if (!optionalProvider.isPresent()) {
            throw new ForbiddenEntityCreationException("The requester is not a provider");
        }

        Provider provider = optionalProvider.get();

        if (!provider.isConfirmed()) {
            throw new ForbiddenEntityCreationException("You must confirm your email");
        }

        AlgorithmProvider algorithmProvider = algorithmProviderRequest.build(provider);

        algorithmProvider = this.algorithmProviderRepository.save(algorithmProvider);

        algorithm.addProvider(algorithmProvider);

        this.repository.save(algorithm);

        return algorithmProvider.toDTO();
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

        final AlgorithmProvider provider = this.getProvider(userPrincipal, pId);

        try {
            /** Fast path, try to delete */
            this.algorithmProviderRepository.delete(provider);
            return provider.toDTO();
        } catch (Exception e) {
            LOGGER.log(Level.FINE, String.format("Fast path for provider %d deletion as fail. Calculate damage", pId), e);
        }

        try {
            /** Close previous transactions */
            this.algorithmProviderRepository.close();
        } catch (Exception e) {
            LOGGER.log(Level.FINE, String.format("Could not close transactions in provider %d deletion process", pId), e);
        }

        /** Notify affected devices */
        List<Training> trainings = trainingRepository.findAllTrainingWithProvider(provider);
        final Set<Device> devices = trainings.stream().map(e -> e.getLocalization().getOwner()).collect(Collectors.toSet());
        final NotifyService notifyService = NotifyService.providerDeletionNotification(devices);
        notifyService.run();

        /**  Delete provider */
        int affectedDevices = trainingRepository.deleteTrainingsAffectedBy(provider);

        LOGGER.log(Level.INFO, String.format("Provider %d deletion affected %d training", pId, affectedDevices));

        this.algorithmProviderRepository.delete(provider);

        return provider.toDTO();
    }


    /**
     * @param userPrincipal
     * @param aId
     * @param pId
     * @param request
     * @return See {@link me.nunum.whereami.model.dto.AlgorithmProviderDTO}
     * @throws EntityNotFoundException        If entity is not present in persist layer
     * @throws ForbiddenEntityAccessException If requester is not the owner of the instance
     * @throws IllegalArgumentException       If the request not contain valid provider properties
     */
    public DTO updateProvider(Principal userPrincipal, Long aId, Long pId, UpdateAlgorithmProvider request) {

        final AlgorithmProvider provider = this.getProvider(userPrincipal, pId);

        return this.algorithmProviderRepository.save(request.updateProvider(provider)).toDTO();
    }


    /**
     * Helper method to obtain provider
     *
     * @param userPrincipal See {@link Principal}
     * @param pId           Provider Id
     * @return See {@link AlgorithmProvider}
     * @throws EntityNotFoundException        If entity is not present in persist layer
     * @throws ForbiddenEntityAccessException If requester is not the owner of the instance
     */
    private AlgorithmProvider getProvider(Principal userPrincipal, Long pId) {

        Device device = this.deviceRepository.findOrPersist(userPrincipal);

        Optional<Provider> optionalProvider = this.providerRepository.findByDevice(device);

        if (!optionalProvider.isPresent()) {
            throw new ForbiddenEntityAccessException("The requester is not a provider");
        }

        Provider provider = optionalProvider.get();

        if (!provider.isConfirmed()) {
            throw new ForbiddenEntityAccessException("You must confirm your email");
        }

        Optional<AlgorithmProvider> algorithmProvider = this.algorithmProviderRepository.findById(pId);

        if (!algorithmProvider.isPresent()) {
            throw new EntityNotFoundException(String.format("Provider %s not found", pId));
        }

        final AlgorithmProvider aProvider = algorithmProvider.get();

        if (!aProvider.belongs(device)) {
            throw new ForbiddenEntityAccessException(String
                    .format("Device %s not has permissions to delete provider %d", userPrincipal.getName(), pId));
        }

        return aProvider;
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
