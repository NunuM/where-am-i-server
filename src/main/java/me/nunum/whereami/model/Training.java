package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.TrainingDTO;

import javax.persistence.*;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Entity
@NamedQuery(
        name = "Training.findAllByLocalization",
        query = "SELECT OBJECT(u) FROM Training u where u.localization.id=:localizationId"
)
public class Training implements DTOable {

    @Id
    @GeneratedValue
    private Long id;

    private String uid;

    @OneToOne
    private Algorithm algorithm;

    private TrainingStatus status;

    @OneToOne
    private Device requester;

    @ManyToOne
    private Localization localization;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    protected Training() {
        //JPA
    }

    public Training(Algorithm algorithm,
                    Localization localization,
                    Device requester) {
        this(algorithm, TrainingStatus.REQUEST, localization, requester);
    }


    public Training(Algorithm algorithm,
                    TrainingStatus status,
                    Localization localization,
                    Device requester) {

        this.uid = UUID.randomUUID().toString();
        this.algorithm = algorithm;
        this.status = status;
        this.requester = requester;
        this.localization = localization;
    }


    @PrePersist
    protected void onCreate() {
        updated = created = Date.from(Instant.now());
    }

    @PreUpdate
    protected void onUpdate() {
        updated = Date.from(Instant.now());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Training)) return false;

        Training training = (Training) o;

        if (!uid.equals(training.uid)) return false;
        return created.equals(training.created);
    }

    @Override
    public int hashCode() {
        int result = uid.hashCode();
        result = 31 * result + created.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", algorithm=" + algorithm +
                ", status=" + status +
                ", requester=" + requester +
                ", localization=" + localization +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    public boolean isAllowedToCheckTheStatus(Device device) {
        return this.requester.equals(device)
                || this.localization.isOwner(device);
    }

    @Override
    public DTO toDTO() {

        return new TrainingDTO(this.id,
                status.toString(),
                algorithm.getName(),
                algorithm.getId(), created, updated);
    }
}
