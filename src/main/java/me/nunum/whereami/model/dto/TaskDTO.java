package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.Map;

public final class TaskDTO implements DTO {


    private HashMap<String, Object> map;

    public TaskDTO(Long id, String status) {
        this.map = new HashMap<>(2);

        this.map.put("id", id);
        this.map.put("state", status);

    }

    @Override
    public Map<String, Object> dtoValues() {
        return this.map;
    }
}
