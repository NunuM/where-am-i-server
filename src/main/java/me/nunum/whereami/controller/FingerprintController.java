package me.nunum.whereami.controller;


import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Fingerprint;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.dto.PositionDTO;
import me.nunum.whereami.model.persistance.FingerprintRepository;
import me.nunum.whereami.model.persistance.LocalizationRepository;
import me.nunum.whereami.model.persistance.PositionRepository;
import me.nunum.whereami.model.persistance.jpa.FingerprintRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.LocalizationRepositoryJpa;
import me.nunum.whereami.model.persistance.jpa.PostitionRepositoryJpa;
import me.nunum.whereami.model.request.FingerprintRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FingerprintController implements AutoCloseable {

    private final FingerprintRepository repository;
    private final PositionRepository positionRepository;
    private final LocalizationRepository localizationRepository;

    public FingerprintController() {
        this.repository = new FingerprintRepositoryJpa();
        this.positionRepository = new PostitionRepositoryJpa();
        this.localizationRepository = new LocalizationRepositoryJpa();
    }

    public DTO storeFingerprints(List<FingerprintRequest> fingerprints) {

        Position position = null;

        List<Fingerprint> fingerprintList = fingerprints
                .stream()
                .map(FingerprintRequest::build)
                .collect(Collectors.toList());

        this.repository.bulkFingerprints(fingerprintList);

        if (!fingerprints.isEmpty()) {

            final Optional<Position> somePosition = positionRepository.findById((long) fingerprints.get(0).getPosition());

            if (somePosition.isPresent()) {
                position = somePosition.get();

                position.incrementSamplesBy((long) fingerprints.size());

                Localization localization = position.getLocalization();
                localization.incrementSample();

                this.localizationRepository.save(localization);

                this.positionRepository.updateMetaData(position);
            }
        }

        if (position == null) {
            return new PositionDTO();
        }


        return position.toDTO();
    }

    @Override
    public void close() throws Exception {
        this.repository.close();
    }
}
