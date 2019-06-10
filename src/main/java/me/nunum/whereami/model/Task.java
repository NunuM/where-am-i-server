package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.TaskDTO;
import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "Task.findTaskByTrainingId",
                query = "SELECT OBJECT(t) FROM Task t WHERE t.training.id=:trainingId"
        ),
        @NamedQuery(
                name = "Task.deleteAllByProviderId",
                query = "DELETE FROM Task t WHERE t.training.algorithmProvider.id=:providerId"
        ),
        @NamedQuery(
                name = "Task.allByStatus",
                query = "SELECT OBJECT(t) FROM Task t WHERE t.state=:st"
        )
})
public class Task implements DTOable {

    @Id
    @GeneratedValue
    @Column(name = "TASK_ID")
    private Long id;


    private int batchSize;

    @Index
    private Long cursor;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "task")
    private Training training;


    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;


    @Temporal(TemporalType.TIMESTAMP)
    private Date finishSinkAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date finishTraingAt;


    @Index
    private STATE state;


    public enum STATE {
        RUNNING {
            @Override
            public String toString() {
                return "running";
            }
        }, FINISH_SINK {
            @Override
            public String toString() {
                return "sink";
            }
        }, FINISH_TRAINING {
            @Override
            public String toString() {
                return "ready for prediction";
            }
        }
    }

    public Task() {
    }

    public Task(Long cursor) {
        this(100, cursor);
    }

    public Task(int batchSize, Long cursor) {
        this.batchSize = batchSize;
        this.cursor = cursor;
        this.state = STATE.RUNNING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public Long getCursor() {
        return cursor;
    }

    public void setCursor(Long cursor) {
        this.cursor = cursor;
    }

    public Training trainingInfo() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getFinishSinkAt() {
        return finishSinkAt;
    }


    public void sinkFinish(Date finishSinkAt) {
        this.state = STATE.FINISH_SINK;
        this.finishSinkAt = finishSinkAt;
    }

    public void trainingFinish() {
        this.finishTraingAt = new Date(System.currentTimeMillis());
        this.state = STATE.FINISH_TRAINING;
        this.training.trainingIsFinish();
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(trainingInfo(), task.trainingInfo()) &&
                getState() == task.getState();
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingInfo(), getState());
    }

    @Override
    public DTO toDTO() {
        return new TaskDTO(this.id, this.state.toString());
    }

}
