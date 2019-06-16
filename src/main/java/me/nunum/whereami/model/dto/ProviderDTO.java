package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.Map;

public final class ProviderDTO implements DTO {

    private HashMap<String, Object> map;

    public ProviderDTO() {
        this.map = new HashMap<>();
    }

    public ProviderDTO(Long id, String email, boolean isConfirmed) {
        this.map = new HashMap<>();

        map.put("id", id);
        map.put("email", email);
        map.put("isEmailConfirmed", isConfirmed);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return map;
    }
}
