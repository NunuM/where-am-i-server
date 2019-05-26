package me.nunum.whereami.model;

import me.nunum.whereami.framework.domain.Identifiable;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.LocalizationDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"label", "user"}))
public class Localization implements DTOable, Identifiable<Long>,Comparable<Localization> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 100)
    private String label;

    private Long samples;

    private Float accuracy;

    private Integer positions;

    private Double latitude;

    private Double longitude;

    @Column(length = 100)
    private String user;

    @ManyToOne
    private Device owner;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "localization")
    private List<Training> trainings;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    protected Localization() {
        //JPA
    }

    public Localization(String label,
                        String userLabel, Device owner) {
        this(label, userLabel, 0.0, 0.0, owner);
    }


    public Localization(String label,
                        String userLabel,
                        Double latitude,
                        Double longitude,
                        Device owner) {

        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.samples = 0L;
        this.accuracy = 0.0f;
        this.positions = 0;
        this.user = userLabel;
        this.owner = owner;
        this.trainings = new ArrayList<>();
    }


    @PrePersist
    protected void onCreate() {
        updated = created = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date(System.currentTimeMillis());
    }

    @Override
    public DTO toDTO() {
        return new LocalizationDTO(this.id,
                this.label,
                this.user,
                this.samples,
                this.accuracy,
                this.positions,
                false
        );
    }

    public DTO toDTO(Device requester) {
        return new LocalizationDTO(this.id,
                this.label,
                this.user,
                this.samples,
                this.accuracy,
                this.positions,
                this.owner.equals(requester)
        );
    }

    @Override
    public boolean is(Long id) {
        return this.id.equals(id);
    }

    @Override
    public Long id() {
        return this.id;
    }

    public boolean isOwner(Device requester) {
        return this.owner.equals(requester);
    }

    public void incrementSample() {
        this.samples += 1;
    }

    public void incrementPosition() {
        this.positions += 1;
    }

    public void decrementPosition() {
        this.positions -= 1;
    }

    public boolean addTraining(Training training){

        this.trainings.add(training);
        if(training.getLocalization().id() != this.id){
            training.setLocalization(this);
        }

        return true;
    }

    public Device getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Localization that = (Localization) o;
        return Objects.equals(label, that.label) &&
                Objects.equals(samples, that.samples) &&
                Objects.equals(accuracy, that.accuracy) &&
                Objects.equals(positions, that.positions) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude) &&
                Objects.equals(user, that.user) &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, samples, accuracy, positions, latitude, longitude, user, owner);
    }

    @Override
    public String toString() {
        return "Localization{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", samples=" + samples +
                ", accuracy=" + accuracy +
                ", positions=" + positions +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", user='" + user + '\'' +
                ", owner=" + owner +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    @Override
    public int compareTo(Localization o) {
        return this.label.compareToIgnoreCase(o.label);
    }
}
