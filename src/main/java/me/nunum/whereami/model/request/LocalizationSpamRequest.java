package me.nunum.whereami.model.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LocalizationSpamRequest {

    @NotNull
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    private String className;

    protected LocalizationSpamRequest() {
        this(0L);
    }

    public LocalizationSpamRequest(Long id) {
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