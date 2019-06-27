package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.Map;

public class AlgorithmImplementationDTO implements DTO {

    private final HashMap<String,Object> map;

    public AlgorithmImplementationDTO(Long predictedPosition, Double accuracy) {
        this.map = new HashMap<>(2);

        this.map.put("positionId", predictedPosition);
        this.map.put("accuracy", accuracy);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return map;
    }
}
