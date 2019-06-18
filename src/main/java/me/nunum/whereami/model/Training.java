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
@NamedQueries({
        @NamedQuery(
                name = "Training.findAllByLocalization",
                query = "SELECT OBJECT(u) FROM Training u where u.localization.id=:localizationId ORDER BY u.updated DESC"),
        @NamedQuery(
                name = "Training.findAllByProviderId",
                query = "SELECT OBJECT(u) FROM Training u where u.algorithmProvider.id=:providerId"
        ),
        @NamedQuery(
                name = "Training.findTrainingByProviderAndAlgorithmId",
                query = "SELECT OBJECT(u) FROM Training u where u.algorithm.id=:algorithmId AND u.algorithmProvider.id=:algorithmProviderId AND u.localization.id=:localizationId"
        ),
        @NamedQuery(
                name = "Training.deleteAllByProviderId",
                query = "DELETE FROM Training u where u.algorithmProvider.id=:providerId"
        )
})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"ALGORITHM_ALG_ID", "ALGORITHMPROVIDER_ID", "LOCALIZATION_ID"}))
public class Training implements DTOable {

    @Id
    @GeneratedValue
    private Long id;

    private String uid;

    @ManyToOne(fetch = FetchType.LAZY)
    private Algorithm algorithm;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private AlgorithmProvider algorithmProvider;

    private TrainingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Localization localization;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TASK_ID")
    private Task task;

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
        this.task = new Task(0L);
    }

    public Long getId() {
        return id;
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

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public AlgorithmProvider getAlgorithmProvider() {
        return algorithmProvider;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean isFinished() {
        return this.status.equals(TrainingStatus.FINISHED);
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


    public boolean isAllowedToCheckTheStatus(Device device) {
        return this.localization.isOwner(device);
    }

    @Override
    public DTO toDTO() {

        return new TrainingDTO(this.id,
                status.toString(),
                algorithm.getName(),
                algorithm.getId(),
                algorithmProvider.getId(),
                created,
                updated);
    }

    public void resetState() {
        this.status = TrainingStatus.REQUEST;
    }
}
