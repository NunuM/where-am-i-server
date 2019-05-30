package me.nunum.whereami.model.request;

import javax.validation.constraints.NotNull;

public class PositionSpamRequest {

    @NotNull
    private Long id;

    private String className;

    public PositionSpamRequest() {
        this(0L);
    }

    public PositionSpamRequest(Long id) {
        this.id = id;
        this.className = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "PositionSpamRequest{" +
                "id=" + id +
                ", className='" + className + '\'' +
                '}';
    }
}
