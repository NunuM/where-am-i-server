package me.nunum.whereami.controller;


import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Fingerprint;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.dto.PositionDTO;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.FingerprintRepository;
import me.nunum.whereami.model.persistance.LocalizationRepository;
import me.nunum.whereami.model.persistance.PositionRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.FingerprintRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.LocalizationRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.PositionRepositoryJpa;
import me.nunum.whereami.model.request.FingerprintRequest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FingerprintController implements AutoCloseable {

    private final FingerprintRepository repository;
    private final DeviceRepository deviceRepository;
    private final PositionRepository positionRepository;
    private final LocalizationRepository localizationRepository;

    public FingerprintController() {
        this.repository = new FingerprintRepositoryJpa();
        this.deviceRepository = new DeviceRepositoryJpa();
        this.positionRepository = new PositionRepositoryJpa();
        this.localizationRepository = new LocalizationRepositoryJpa();
    }

    /**
     * Bulk insert a list of samples
     *
     * @param fingerprints List to persist
     * @return List of {@link PositionDTO}
     */
    public List<DTO> storeFingerprints(Principal principal, List<FingerprintRequest> fingerprints) {

        Device device = this.deviceRepository.findOrPersist(principal);

        Position position = null;
        List<DTO> dtos = new ArrayList<>();

        List<FingerprintRequest> distinct = fingerprints.stream().distinct().collect(Collectors.toList());

        for (FingerprintRequest request : distinct) {

            List<Fingerprint> fingerprintList = fingerprints
                    .stream()
                    .filter(e -> e.getPositionId() == request.getPositionId() && e.getLocalizationId() == request.getLocalizationId())
                    .map(FingerprintRequest::build)
                    .collect(Collectors.toList());


            if (!fingerprintList.isEmpty()) {

                final Optional<Position> somePosition = positionRepository.findById(request.getPositionId());

                if (somePosition.isPresent()) {

                    position = somePosition.get();


                    Localization localization = position.getLocalization();

                    if (localization.canOtherUsersSendSamples() || localization.isOwner(device)) {
                        this.repository.bulkFingerprints(fingerprintList);
                    } else {
                        continue;
                    }

                    position.incrementSamplesBy((long) fingerprints.size());

                    localization.incrementSample();

                    this.localizationRepository.save(localization);

                    this.positionRepository.updateMetaData(position);

                    dtos.add(position.toDTO());
                }
            }
        }
        return dtos;
    }

    @Override
    public void close() throws Exception {
        this.repository.close();
    }
}
