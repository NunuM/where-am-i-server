package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.PositionSpamReport;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityModificationException;
import me.nunum.whereami.model.persistance.*;
import me.nunum.whereami.model.persistance.jpa.*;
import me.nunum.whereami.model.request.NewPositionRequest;
import me.nunum.whereami.model.request.PostionSpamRequest;

import javax.ws.rs.core.SecurityContext;
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


    public List<DTO> positions() {

        return this.repository.positionsByLocalization(this.localization)
                .stream()
                .map(Position::toDTO)
                .collect(Collectors.toList());

    }

    public DTO newPosition(SecurityContext securityContext,
                           NewPositionRequest newPositionRequest) {

        final Device requester = this.deviceRepository.findOrPersist(securityContext.getUserPrincipal());

        if (!this.localization.isOwner(requester)) {
            throw new ForbiddenEntityModificationException(
                    String.format("Device %s request position insertion without permission",
                            securityContext.getUserPrincipal().getName())
            );
        }

        this.localization.incrementPosition();
        this.localizationRepository.save(this.localization);

        return this.repository.save(newPositionRequest.buildPosition(this.localization)).toDTO();
    }

    public DTO deletePosition(SecurityContext securityContext, Long ip) {

        final Optional<Position> somePosition = this.repository.findById(ip);
        final Device requester = this.deviceRepository.findOrPersist(securityContext.getUserPrincipal());


        if (!somePosition.isPresent()) {
            throw new EntityNotFoundException(
                    String.format("Position %d for localization %d requested by %s does not exists",
                            ip,
                            localization.id(),
                            securityContext.getUserPrincipal().getName())
            );
        }

        if (!this.localization.isOwner(requester)) {
            throw new ForbiddenEntityModificationException(
                    String.format("Device %s request position insertion without permission",
                            securityContext.getUserPrincipal().getName())
            );
        }

        this.localization.decrementPosition();

        this.localizationRepository.save(this.localization);

        final Position position = somePosition.get();

        this.repository.delete(position);

        this.fingerprintRepository.deleteByPosition(position);

        return position.toDTO();
    }

    public DTO processSpamRequest(Principal userPrincipal, PostionSpamRequest request) {

        Optional<Position> optionalPosition = this.repository.findById(request.getId());

        if (!optionalPosition.isPresent()) {
            throw new EntityNotFoundException(
                    String.format("Spam report for localization %d requested by %s does not exists",
                            request.getId(),
                            userPrincipal.getName())
            );
        }

        final Position position = optionalPosition.get();

        PositionSpamReport positionSpamReport = this.positionSpamRepository.findOrCreateByPosition(position);

        positionSpamReport.newReport(this.deviceRepository.findOrPersist(userPrincipal));

        return this.positionSpamRepository.save(positionSpamReport).toDTO();

    }

    @Override
    public void close() throws Exception {
        this.repository.close();
    }
}
