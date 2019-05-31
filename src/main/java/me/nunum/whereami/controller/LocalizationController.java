package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.LocalizationSpamReport;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.exceptions.ForbiddenEntityDeletionException;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.LocalizationRepository;
import me.nunum.whereami.model.persistance.LocalizationSpamRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.LocalizationRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.LocalizationSpamRepositoryJpa;
import me.nunum.whereami.model.request.LocalizationSpamRequest;
import me.nunum.whereami.model.request.NewLocalizationRequest;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalizationController implements AutoCloseable {

    private final LocalizationRepository repository;
    private final DeviceRepository deviceRepository;
    private final LocalizationSpamRepository spamRepository;

    public LocalizationController() {
        this.repository = new LocalizationRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
        this.spamRepository = new LocalizationSpamRepositoryJpa();
    }

    public List<DTO> localizations(final Principal principal,
                                   Optional<Integer> page,
                                   Optional<String> localizationName) {

        final Device requester = this.deviceRepository.findOrPersist(principal);

        return this.repository
                .searchWithPagination(requester, page, localizationName)
                .stream()
                .map(e -> e.toDTO(requester))
                .collect(Collectors.toList());
    }

    public DTO newLocalization(final Principal principal,
                               final NewLocalizationRequest request) {

        final Device device = this.deviceRepository.findOrPersist(principal);

        return this.repository.save(request.buildLocalization(device)).toDTO(device);
    }

    public DTO newSpamReport(Principal userPrincipal,
                             LocalizationSpamRequest spamRequest) {

        final Optional<Localization> someLocalization = this.repository.findById(spamRequest.getId());

        if (!someLocalization.isPresent()) {
            throw new EntityNotFoundException(
                    String.format("Spam report for localization %d requested by %s does not exists",
                            spamRequest.getId(),
                            userPrincipal.getName())
            );
        }

        final Localization theLocalization = someLocalization.get();

        final LocalizationSpamReport localizationSpamReport = this.spamRepository.findOrCreateByLocalization(theLocalization);

        localizationSpamReport.newReport(this.deviceRepository.findOrPersist(userPrincipal));

        return this.spamRepository.save(localizationSpamReport).toDTO();
    }

    public DTO deleteLocalizationRequest(Principal userPrincipal, Long localizationId) {

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


    public Localization localization(Long localizationId) {
        final Optional<Localization> someLocalization = this.repository.findById(localizationId);

        if (!someLocalization.isPresent()) {
            throw new EntityNotFoundException(
                    String.format("Localization %d does not exists", localizationId)
            );
        }

        return someLocalization.get();
    }

    @Override
    public void close() throws Exception {
        this.repository.close();
    }
}
