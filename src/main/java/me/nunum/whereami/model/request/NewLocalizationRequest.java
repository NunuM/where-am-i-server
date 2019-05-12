package me.nunum.whereami.model.request;

import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Localization;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewLocalizationRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String label;

    @NotNull
    private boolean isVisible;

    @NotNull
    @Size(min = 1, max = 255)
    private String user;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    public NewLocalizationRequest() {
        this("", false, "", 0.0, 0.0);
    }

    public NewLocalizationRequest(String label,
                                  boolean isVisible,
                                  String user,
                                  double latitude,
                                  double longitude) {
        this.label = label;
        this.isVisible = isVisible;
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Localization buildLocalization(final Device owner) {
        return new Localization(this.label.toLowerCase(), this.user.toLowerCase(), latitude, longitude, owner);
    }
}
