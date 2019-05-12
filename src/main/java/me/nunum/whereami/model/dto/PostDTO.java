package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.Map;

public final class PostDTO implements DTO {

    private final Map<String, Object> objectMap;

    public PostDTO(Long id, String title, String imageURL, String sourceURL) {
        this.objectMap = new HashMap<>(4);

        objectMap.put("id", id);
        objectMap.put("title", title);
        objectMap.put("imageURL", imageURL);
        objectMap.put("sourceURL", sourceURL);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return objectMap;
    }
}
