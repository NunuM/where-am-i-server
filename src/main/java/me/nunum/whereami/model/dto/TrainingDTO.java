package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class TrainingDTO implements DTO {

    private final Map<String, Object> objectMap;

    public TrainingDTO(final Long id,
                       final String status,
                       final String name,
                       Long algId,
                       Long pId,
                       Date created,
                       Date updated) {

        this.objectMap = new HashMap<>(5);

        HashMap<String, Object> algMap = new HashMap<>();
        algMap.put("id", algId);
        algMap.put("name", name);

        this.objectMap.put("id", id);
        this.objectMap.put("status", status);
        this.objectMap.put("algorithm", algMap);
        this.objectMap.put("algorithmProvider", pId);
        this.objectMap.put("created", created);
        this.objectMap.put("updated", updated);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return objectMap;
    }
}
