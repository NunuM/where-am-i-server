package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.Map;

public final class LocalizationReportDTO implements DTO {


    private final Map<String, Object> objectMap;

    public LocalizationReportDTO(Long id, Integer numberOfReports) {

        this.objectMap = new HashMap<>(2);

        this.objectMap.put("id", id);
        this.objectMap.put("numberOfReports", numberOfReports);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return objectMap;
    }
}
