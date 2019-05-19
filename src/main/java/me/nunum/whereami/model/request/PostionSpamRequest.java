package me.nunum.whereami.model.request;

import javax.validation.constraints.NotNull;

public class PostionSpamRequest {

    @NotNull
    private Long id;

    private String className;

    public PostionSpamRequest() {
        this.id = 0L;
    }

    public PostionSpamRequest(Long id) {
        this.id = id;
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
        return "PostionSpamRequest{" +
                "id=" + id +
                ", className='" + className + '\'' +
                '}';
    }
}
