package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.PositionSpamReport;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityDeletionException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityModificationException;
import me.nunum.whereami.model.persistance.*;
import me.nunum.whereami.model.persistance.jpa.*;
import me.nunum.whereami.model.request.NewPositionRequest;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PositionsController implements AutoCloseable {

    private final Localization localization;
    private final PositionRepository repository;
    private final DeviceRepository deviceRepository;
    private final FingerprintRepository fingerprintRepository;
    private final LocalizationRepository localizationRepository;
    private final PositionSpamRepository positionSpamRepository;


    public PositionsController(Localization localization) {
        this.localization = localization;
        this.repository = new PostitionRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
        this.fingerprintRepository = new FingerprintRepositoryJpa();
        this.localizationRepository = new LocalizationRepositoryJpa();
        this.positionSpamRepository = new PositionSpamRepositoryJpa();
    }


    /**
     * Obtain a list of all positions
     *
     * @return List of {@link me.nunum.whereami.model.dto.PositionDTO}
     */
    public List<DTO> positions() {

        return this.repository.positionsByLocalization(this.localization)
                .stream()
                .map(Position::toDTO)
                .collect(Collectors.toList());

    }

    /**
     * Create new position for a given localization
     *
     * @param principal          See {@link Principal}
     * @param newPositionRequest See {@link NewPositionRequest}
     * @return See {@link me.nunum.whereami.model.dto.PositionDTO}
     * @throws ForbiddenEntityModificationException If is not he owner
     */
    public DTO newPosition(Principal principal,
                           NewPositionRequest newPositionRequest) {

        final Device requester = this.deviceRepository.findOrPersist(principal);

        if (!this.localization.isOwner(requester)) {
            throw new ForbiddenEntityModificationException(
                    String.format("Device %s request position insertion without permission",
                            principal.getName())
            );
        }

        this.localization.incrementPosition();
        this.localizationRepository.save(this.localization);

        return this.repository.save(newPositionRequest.buildPosition(this.localization)).toDTO();
    }


    /**
     * Delete position
     *
     * @param principal See {@link Principal}
     * @param ip        Position Id
     * @return See {@link me.nunum.whereami.model.dto.PositionDTO}
     * @throws EntityNotFoundException              If the given Id does not one match
     * @throws ForbiddenEntityModificationException If is not the Owner
     */
    public DTO deletePosition(Principal principal, Long ip) {

        final Optional<Position> somePosition = this.repository.findById(ip);
        final Device requester = this.deviceRepository.findOrPersist(principal);


        if (!somePosition.isPresent()) {
            throw new EntityNotFoundException(
                    String.format("Position %d for localization %d requested by %s does not exists",
                            ip,
                            localization.id(),
                            principal.getName())
            );
        }

        if (!this.localization.isOwner(requester)) {
            throw new ForbiddenEntityDeletionException(
                    String.format("Device %s request position insertion without permission",
                            principal.getName())
            );
        }

        this.localization.decrementPosition();

        this.localizationRepository.save(this.localization);

        final Position position = somePosition.get();

        this.repository.delete(position);

        this.fingerprintRepository.deleteByPosition(position);

        return position.toDTO();
    }

    /**
     * Create new spam report
     *
     * @param userPrincipal See {@link Principal}
     * @return See {@link DTO}
     */
    public DTO processSpamRequest(Principal userPrincipal, Position position) {

        position.addSpamReport(this.deviceRepository.findOrPersist(userPrincipal));

        this.repository.save(position);

        return position.getSpamReport().toDTO();

    }


    public Position position(Principal principal, Long positionId) {

        Optional<Position> optionalPosition = this.repository.findById(positionId);

        if (!optionalPosition.isPresent()) {
            throw new EntityNotFoundException(
                    String.format("Spam report for localization %d requested by %s does not exists",
                            positionId,
                            principal.getName())
            );
        }

        return optionalPosition.get();
    }


    @Override
    public void close() throws Exception {
        this.repository.close();
    }
}
