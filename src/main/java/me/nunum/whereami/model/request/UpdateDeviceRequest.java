package me.nunum.whereami.model.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdateDeviceRequest {

    @NotNull
    @Size(max = 255)
    private String firebaseToken;

    public UpdateDeviceRequest() {
        this("");
    }

    public UpdateDeviceRequest(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }


    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    @Override
    public String toString() {
        return "UpdateDeviceRequest{" +
                "firebaseToken='" + firebaseToken + '\'' +
                '}';
    }
}
