package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.AlgorithmProvider;

import java.util.HashMap;
import java.util.Map;

public class AlgorithmProviderDTO implements DTO {

    private HashMap<String, Object> map;

    public AlgorithmProviderDTO(Long id, String email, AlgorithmProvider.METHOD method, Map<String, String> properties) {
        this.map = new HashMap<>(4);

        map.put("id", id);
        map.put("email", email);
        map.put("method", method.toString());
        map.put("properties", properties);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return map;
    }
}
