package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.FeedbackDTO;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
public class Feedback implements DTOable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Device device;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public Feedback() {
    }

    public Feedback(String contact, String message, Device device) {
        this.contact = contact;
        this.message = message;
        this.device = device;
        this.created = new Date();
    }

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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return Objects.equals(device, feedback.device) &&
                Objects.equals(created, feedback.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(device, created);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "contact='" + contact + '\'' +
                ", message='" + message + '\'' +
                ", created=" + created +
                '}';
    }

    @Override
    public DTO toDTO() {
        return new FeedbackDTO(this.id, this.contact, this.message, this.created);
    }
}
