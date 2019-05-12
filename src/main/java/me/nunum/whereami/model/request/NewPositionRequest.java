package me.nunum.whereami.model.request;

import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewPositionRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String label;

    public NewPositionRequest() {
        this("");
    }

    public NewPositionRequest(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "NewPositionRequest{" +
                "label='" + label + '\'' +
                '}';
    }


    public Position buildPosition(Localization localization) {
        return new Position(this.label, localization);
    }
}
