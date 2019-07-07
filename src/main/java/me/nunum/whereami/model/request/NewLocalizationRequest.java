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
    private Boolean publicForTraining;

    @NotNull
    private Boolean canOtherUsersSendSamples;

    @NotNull
    private Boolean publicForPrediction;

    @NotNull
    @Size(min = 3, max = 255)
    private String user;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    public NewLocalizationRequest() {
        this("", false, false, false, "", 0.0, 0.0);
    }

    public NewLocalizationRequest(String label,
                                  boolean isPublicForTraining,
                                  boolean CanOtherUserSendSamples,
                                  boolean isPublicForPrediction,
                                  String user,
                                  double latitude,
                                  double longitude) {
        this.label = label;
        this.publicForTraining = isPublicForTraining;
        this.canOtherUsersSendSamples = CanOtherUserSendSamples;
        this.publicForPrediction = isPublicForPrediction;
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

    public void setPublicForTraining(boolean publicForTraining) {
        this.publicForTraining = publicForTraining;
    }

    public void setCanOtherUsersSendSamples(boolean canOtherUsersSendSamples) {
        this.canOtherUsersSendSamples = canOtherUsersSendSamples;
    }

    public void setPublicForPrediction(boolean publicForPrediction) {
        this.publicForPrediction = publicForPrediction;
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
        return new Localization(this.label.trim().toLowerCase(),
                this.user.trim().toLowerCase(),
                latitude,
                longitude,
                publicForTraining,
                canOtherUsersSendSamples,
                publicForPrediction,
                owner);
    }
}
