package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.Map;

public final class LocalizationDTO implements DTO {

    private final Map<String, Object> objectMap;


    public LocalizationDTO(Long id,
                           String label,
                           String userLabel,
                           Long samples,
                           Float accuracy,
                           Integer positions,
                           boolean isOwner) {

        this.objectMap = new HashMap<>(4);

        final Map<String, Object> stats = new HashMap<>(3);

        this.objectMap.put("id", id);
        this.objectMap.put("label", label);
        this.objectMap.put("user", userLabel);
        this.objectMap.put("isOwner", isOwner);

        stats.put("samples", samples);
        stats.put("accuracy", accuracy);
        stats.put("positions", positions);

        this.objectMap.put("stats", stats);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return objectMap;
    }
}
