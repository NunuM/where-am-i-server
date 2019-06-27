package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Prediction;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityDeletionException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityModificationException;
import me.nunum.whereami.model.exceptions.ForbiddenSubResourceException;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.LocalizationRepository;
import me.nunum.whereami.model.persistance.LocalizationSpamRepository;
import me.nunum.whereami.model.persistance.PredictionRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.LocalizationRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.LocalizationSpamRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.PredictionRepositoryJpa;
import me.nunum.whereami.model.request.NewLocalizationRequest;
import me.nunum.whereami.model.request.NewPredictionRequest;
import me.nunum.whereami.model.request.UpdatePredictionRequest;
import me.nunum.whereami.service.OnlinePhaseService;
import me.nunum.whereami.service.TaskManager;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalizationController implements AutoCloseable {

    private final LocalizationRepository repository;
    private final DeviceRepository deviceRepository;

    /**
     * Constructor
     */
    public LocalizationController() {
        this.repository = new LocalizationRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
    }

    /**
     * Paginate localizations
     *
     * @param principal        See {@link Principal}
     * @param page             Nullable Page
     * @param localizationName Nullable name for search
     * @return List of {@link me.nunum.whereami.model.dto.LocalizationDTO}
     */
    public List<DTO> localizations(final Principal principal,
                                   final Optional<Integer> page,
                                   final Optional<String> localizationName,
                                   final Optional<String> trained,
                                   final Optional<Boolean> onlyUserLocalizations) {

        final Device requester = this.deviceRepository.findOrPersist(principal);

        return this.repository
                .searchWithPagination(requester, page, localizationName, trained, onlyUserLocalizations)
                .stream()
                .map(e -> e.toDTO(requester))
                .collect(Collectors.toList());
    }

    /**
     * Create new localization
     *
     * @param principal See {@link Principal}
     * @param request   See {@link NewLocalizationRequest}
     * @return See  {@link me.nunum.whereami.model.dto.LocalizationDTO}
     * @throws me.nunum.whereami.model.exceptions.EntityAlreadyExists Try to persist the same localization name
     *                                                                for a given user
     */
    public DTO newLocalization(final Principal principal,
                               final NewLocalizationRequest request) {

        final Device device = this.deviceRepository.findOrPersist(principal);

        return this.repository.save(request.buildLocalization(device)).toDTO(device);
    }


    /**
     * Delete a specific localization, this will cascade, meaning, all associated positions
     * and training requests will be deleted.
     *
     * @param userPrincipal  See {@link Principal}
     * @param localizationId Id of the localization
     * @return See  {@link me.nunum.whereami.model.dto.LocalizationDTO}
     * @throws EntityNotFoundException          Localization does not exists
     * @throws ForbiddenEntityDeletionException Requester is not the owner of entity
     */
    public DTO deleteLocalizationRequest(final Principal userPrincipal, final Long localizationId) {

        final Optional<Localization> someLocalization = this.repository.findById(localizationId);

        if (!someLocalization.isPresent()) {
            throw new EntityNotFoundException(
                    String.format("Spam report for localization %d requested by %s does not exists",
                            localizationId,
                            userPrincipal.getName())
            );
        }

        final Device requester = this.deviceRepository.findOrPersist(userPrincipal);

        final Localization theLocalization = someLocalization.get();

        if (!theLocalization.isOwner(requester)) {

            throw new ForbiddenEntityDeletionException(
                    String.format("Device %s request localization deletion without permission",
                            userPrincipal.getName())
            );
        }

        this.repository.delete(theLocalization);

        return theLocalization.toDTO();
    }


    /**
     * Report a specific localization
     *
     * @param userPrincipal See {@link Principal}
     * @param localization  See {@link Localization}
     * @return See {@link me.nunum.whereami.model.dto.LocalizationReportDTO}
     * @throws EntityNotFoundException Localization does not exists
     */
    public DTO newSpamReport(final Principal userPrincipal,
                             final Localization localization) {

        localization.addSpamReporter(this.deviceRepository.findOrPersist(userPrincipal));

        this.repository.save(localization);

        return localization.getSpamReport().toDTO();
    }


    /**
     * Retrieve localization by their Id
     *
     * @param localizationId Localization Id
     * @return see {@link Localization}
     * @throws EntityNotFoundException If localization not exists
     */
    public Localization localization(final Principal principal, final Long localizationId) {

        final Device device = this.deviceRepository.findOrPersist(principal);

        final Optional<Localization> someLocalization = this.repository.findById(localizationId);

        if (!someLocalization.isPresent()) {
            throw new EntityNotFoundException(
                    String.format("Localization %d does not exists", localizationId)
            );
        }

        final Localization localization = someLocalization.get();

        if (localization.isPublic() || localization.isOwner(device)) {
            return localization;
        }

        throw new ForbiddenSubResourceException(String.format("Device %s is not allowed to the sub-resource", device.getId()));
    }


    public List<DTO> requestNewPrediction(Principal userPrincipal, Long id, NewPredictionRequest request) {

        final Localization localization = this.localization(userPrincipal, id);

        final PredictionRepository predictionRepository = new PredictionRepositoryJpa();

        if (!request.isOnlyPolling()) {

            Long requestId = predictionRepository.maxRequestIdForLocalization(localization) + 1;

            final OnlinePhaseService onlinePhaseService = new OnlinePhaseService(localization, requestId, request.getSamples());

            TaskManager.getInstance().queue(onlinePhaseService);
        }

        return predictionRepository
                .allPredictionsSince(localization, request.getLastUpdate())
                .stream()
                .map(Prediction::toDTO)
                .collect(Collectors.toList());
    }


    public DTO processPredictionFeedback(Principal userPrincipal, Long localizationId, Long predictionId, UpdatePredictionRequest request) {

        final Localization localization = this.localization(userPrincipal, localizationId);

        final PredictionRepositoryJpa predictions = new PredictionRepositoryJpa();

        final Optional<Prediction> optionalPrediction = predictions.findById(predictionId);

        if (!optionalPrediction.isPresent()) {
            throw new EntityNotFoundException(String.format("Prediction %d does not exists", predictionId));
        }

        final Prediction prediction = optionalPrediction.get();

        if (!prediction.isSameLocalization(localization)) {
            throw new ForbiddenEntityModificationException(String.format("Prediction %d is not associated with localization %d", localizationId, predictionId));
        }

        request.updateFeebdack(prediction);

        return predictions.save(prediction).toDTO();
    }


    @Override
    public void close() throws Exception {
        this.repository.close();
    }


}
