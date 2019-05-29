package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AlgorithmDTO implements DTO {

    private final Map<String, Object> objectMap;

    public AlgorithmDTO(Long id,
                        String name,
                        String authorName,
                        String paperURL,
                        boolean isApproved,
                        List<Map<String, Object>> providers) {

        this.objectMap = new HashMap<>(6);

        objectMap.put("id", id);
        objectMap.put("authorName", authorName);
        objectMap.put("name", name);
        objectMap.put("paperURL", paperURL);
        objectMap.put("isApproved", isApproved);
        objectMap.put("providers", providers);

    }

    @Override
    public Map<String, Object> dtoValues() {
        return objectMap;
    }
}
