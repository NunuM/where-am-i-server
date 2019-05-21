package me.nunum.whereami.model;

import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity

public class Task {

    @Id
    @GeneratedValue
    public Long id;


    private int batchSize;


    private Long cursor;


    private Long localizationId;


    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;


    @Temporal(TemporalType.TIMESTAMP)
    private Date finish;


    @Index
    private STATE state;


    public static enum STATE {
        RUNNING, FINISH
    }

    public Task() {
    }

    public Task(Long cursor, Long localizationId) {
        this(100, cursor, localizationId);
    }

    public Task(int batchSize, Long cursor, Long localizationId) {
        this.batchSize = batchSize;
        this.cursor = cursor;
        this.localizationId = localizationId;
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

    public Long getLocalizationId() {
        return localizationId;
    }

    public void setLocalizationId(Long localizationId) {
        this.localizationId = localizationId;
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

    public Date getFinish() {
        return finish;
    }

    public void setFinish(Date finish) {
        this.finish = finish;
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
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return batchSize == task.batchSize &&
                Objects.equals(cursor, task.cursor) &&
                Objects.equals(localizationId, task.localizationId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(batchSize, cursor, localizationId);
    }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", batchSize=" + batchSize +
                ", cursor=" + cursor +
                ", localizationId=" + localizationId +
                '}';
    }
}
