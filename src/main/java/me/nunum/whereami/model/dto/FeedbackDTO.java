package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FeedbackDTO implements DTO {


    private final HashMap<String, Object> map;

    public FeedbackDTO(long id, String contact, String message, Date created) {
        this.map = new HashMap<>(4);

        this.map.put("id", id);
        this.map.put("contact", contact);
        this.map.put("message", message);
        this.map.put("created", created);
    }

    @Override
    public Map<String, Object> dtoValues() {
        return this.map;
    }
}
