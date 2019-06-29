package me.nunum.whereami.model.request;

import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Feedback;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NewFeedbackRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String contact;

    @NotNull
    @Size(max = 2000, min = 1)
    private String message;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Feedback build(Device device) {
        return new Feedback(contact, message, device);
    }

    @Override
    public String toString() {
        return "NewFeedbackRequest{" +
                "contact='" + contact + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
