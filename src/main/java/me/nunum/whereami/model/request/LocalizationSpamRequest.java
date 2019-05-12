package me.nunum.whereami.model.request;

import javax.validation.constraints.NotNull;

public class LocalizationSpamRequest {

    @NotNull
    private Long id;

    private String className;

    public LocalizationSpamRequest() {
        this.id = 0L;
    }

    public LocalizationSpamRequest(Long id) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalizationSpamRequest)) return false;

        LocalizationSpamRequest that = (LocalizationSpamRequest) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "LocalizationSpamRequest{" +
                "id=" + id +
                '}';
    }
}