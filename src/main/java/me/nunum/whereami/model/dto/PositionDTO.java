package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.Map;

public final class PositionDTO implements DTO {

    private final Map<String, Object> objectMap;


    public PositionDTO(){
        this(0L,"",0L,0,0,"");
    }

    public PositionDTO(Long id,
                       String label,
                       Long samples,
                       Integer routers,
                       Integer networks,
                       String strongestSignal) {

        this.objectMap = new HashMap<>(3);

        objectMap.put("id", id);
        objectMap.put("label", label);

        final Map<String, Object> statsMap = new HashMap<>(4);

        statsMap.put("samples", samples);
        statsMap.put("routers", routers);
        statsMap.put("networks", networks);
        statsMap.put("strongestSignal", strongestSignal);

        objectMap.put("stats", statsMap);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return objectMap;
    }
}
