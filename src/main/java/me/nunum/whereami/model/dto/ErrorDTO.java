package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.HashMap;
import java.util.Map;

public class ErrorDTO implements DTO {


    private HashMap<String, Object> map = new HashMap<>();

    public ErrorDTO(String message) {
        this.map.put("message", message);
    }

    public static HashMap<String, Object> fromError(Throwable e) {
        return new ErrorDTO(e.getMessage()).dtoValues();
    }

    public static HashMap<String, Object> fromXAppMissingHeader() {
        return new ErrorDTO("X-APP header is required").dtoValues();
    }

    @Override
    public HashMap<String, Object> dtoValues() {
        return this.map;
    }
}
