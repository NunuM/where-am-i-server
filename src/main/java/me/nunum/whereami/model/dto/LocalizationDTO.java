package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class LocalizationDTO implements DTO {

    private final Map<String, Object> objectMap;


    public LocalizationDTO(Long id,
                           String label,
                           String userLabel,
                           Long samples,
                           Integer numberOfModels,
                           Integer positions,
                           boolean isOwner,
                           boolean canOthersSendSamples,
                           Date created) {

        this.objectMap = new HashMap<>(6);

        final Map<String, Object> stats = new HashMap<>(3);

        this.objectMap.put("id", id);
        this.objectMap.put("label", label);
        this.objectMap.put("user", userLabel);
        this.objectMap.put("isOwner", isOwner);
        this.objectMap.put("canOthersSendSamples", canOthersSendSamples);
        this.objectMap.put("created", created);

        stats.put("samples", samples);
        stats.put("numberOfTrainedModels", numberOfModels);
        stats.put("positions", positions);

        this.objectMap.put("stats", stats);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return objectMap;
    }
}
