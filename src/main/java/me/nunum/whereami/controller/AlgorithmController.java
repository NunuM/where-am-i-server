package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Algorithm;
import me.nunum.whereami.model.exceptions.EntityNotFoundException;
import me.nunum.whereami.model.persistance.AlgorithmRepository;
import me.nunum.whereami.model.persistance.jpa.AlgorithmRepositoryJpa;
import me.nunum.whereami.model.request.NewAlgorithmRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AlgorithmController {

    private final AlgorithmRepository repository;

    public AlgorithmController() {
        this.repository = new AlgorithmRepositoryJpa();
    }

    public List<DTO> algorithms(Optional<Integer> page) {

        return this.repository
                .paginate(page)
                .stream()
                .map(Algorithm::toDTO)
                .collect(Collectors.toList());

    }

    public DTO addNewAlgorithm(NewAlgorithmRequest algorithmRequest) {
        return this.repository.save(algorithmRequest.build()).toDTO();
    }

    public DTO algorithm(Long aId) {

        Optional<Algorithm> algorithmOptional = this.repository.findById(aId);

        if (algorithmOptional.isPresent()) {
            return algorithmOptional.get().toDTO();
        }

        throw new EntityNotFoundException(String.format("Algorithm with id:%d, was not found", aId));
    }
}
