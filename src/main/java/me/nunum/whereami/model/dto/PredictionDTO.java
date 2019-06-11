package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.Map;

public class PredictionDTO implements DTO {


    private HashMap<String, Object> map;

    public PredictionDTO(Long id, Long localizationId, String predictionLabel, Long requestId, Long predictedPosition, Float accuracy, Long providerId) {
        this.map = new HashMap<>(7);

        this.map.put("id", id);
        this.map.put("localizationId", localizationId);
        this.map.put("requestId", requestId);
        this.map.put("accuracy", accuracy);
        this.map.put("providerId", providerId);
        this.map.put("predictedPositionId", predictedPosition);
        this.map.put("predictionLabel", predictionLabel);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return this.map;
    }
}
