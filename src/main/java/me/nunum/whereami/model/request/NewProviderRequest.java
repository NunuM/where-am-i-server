package me.nunum.whereami.model.request;

import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Provider;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class NewProviderRequest {

    @NotNull
    @Email
    public String email;

    public NewProviderRequest() {
    }

    public NewProviderRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Provider build(Device device) {
        return new Provider(email, UUID.randomUUID().toString(), false, device);
    }

    @Override
    public String toString() {
        return "NewProviderRequest{" +
                "email='" + email + '\'' +
                '}';
    }
}
