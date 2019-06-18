package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.Map;

public class DeviceDTO implements DTO {

    private final HashMap<String, Object> map;

    public DeviceDTO(String token) {
        this.map = new HashMap<>(1);

        map.put("firebaseToken", token);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return map;
    }
}
