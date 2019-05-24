package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.TrainingDTO;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@NamedQuery(
        name = "Training.findAllByLocalization",
        query = "SELECT OBJECT(u) FROM Training u where u.localization.id=:localizationId"
)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"ALGORITHM_ALG_ID", "ALGORITHMPROVIDER_ID"}))
public class Training implements DTOable {

    @Id
    @GeneratedValue
    private Long id;

    private String uid;

    @ManyToOne
    private Algorithm algorithm;

    @ManyToOne
    private AlgorithmProvider algorithmProvider;

    private TrainingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Localization localization;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    protected Training() {
        //JPA
    }


    public Training(Algorithm algorithm,
                    AlgorithmProvider provider,
                    Localization localization) {
        this(algorithm, provider, TrainingStatus.REQUEST, localization);
    }

    public Training(Algorithm algorithm,
                    AlgorithmProvider provider,
                    TrainingStatus status,
                    Localization localization) {

        this.uid = UUID.randomUUID().toString();
        this.algorithm = algorithm;
        this.status = status;
        this.localization = localization;
        this.algorithmProvider = provider;
    }

    public Long localizationAssociated() {
        return localization.id();
    }

    public boolean isHTTPProvider() {
        return this.algorithmProvider.getMethod().equals(AlgorithmProvider.METHOD.HTTP);
    }

    public void trainingInProgress() {
        this.status = TrainingStatus.PROGRESS;
    }

    public Map<String, String> providerProperties() {
        return this.algorithmProvider.getProperties();
    }

    public void trainingIsFinish() {
        this.status = TrainingStatus.FINISHED;
    }

    public Localization getLocalization() {
        return localization;
    }

    public AlgorithmProvider getAlgorithmProvider() {
        return algorithmProvider;
    }

    public void setLocalization(Localization localization) {
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
        return uid.equals(training.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", algorithm=" + algorithm +
                ", status=" + status +
                ", localization=" + localization +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    public boolean isAllowedToCheckTheStatus(Device device) {
        return this.localization.isOwner(device);
    }

    @Override
    public DTO toDTO() {

        return new TrainingDTO(this.id,
                status.toString(),
                algorithm.getName(),
                algorithm.getId(), created, updated);
    }
}
