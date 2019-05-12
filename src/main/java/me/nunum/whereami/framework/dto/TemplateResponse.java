package me.nunum.whereami.framework.dto;

public class TemplateResponse {

    private String value;

    public TemplateResponse() {
    }

    public TemplateResponse(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
