package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.Map;

public final class AlgorithmDTO implements DTO {

    private final Map<String, Object> objectMap;

    public AlgorithmDTO(Long id,
                        String name,
                        String authorName,
                        String paperURL) {

        this.objectMap = new HashMap<>(5);

        objectMap.put("id", id);
        objectMap.put("authorName", authorName);
        objectMap.put("name", name);
        objectMap.put("paperURL", paperURL);

    }

    @Override
    public Map<String, Object> dtoValues() {
        return objectMap;
    }
}
