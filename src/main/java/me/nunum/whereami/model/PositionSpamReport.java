package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@NamedQuery(
        name = "PositionSpam.findByPositionId",
        query = "SELECT OBJECT(u) FROM PositionSpamReport u where u.position.id=:positionId"
)
public class PositionSpamReport implements DTOable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.REFRESH)
    private List<Device> reporters;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "spamReport")
    private Position position;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public PositionSpamReport() {
    }

    public PositionSpamReport(Position position) {
        this.position = position;
        this.reporters = new ArrayList<>();
    }

    @PrePersist
    protected void onCreate() {
        updated = created = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date(System.currentTimeMillis());
    }

    public boolean newReport(Device device) {
        return this.reporters.add(device);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionSpamReport that = (PositionSpamReport) o;
        return Objects.equals(position, that.position) &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, created);
    }

    @Override
    public DTO toDTO() {
        return null;
    }
}
