package me.nunum.whereami.model;

import me.nunum.whereami.framework.domain.Identifiable;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.PositionDTO;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@NamedQuery(
        name = "Position.findByLocalizationId",
        query = "SELECT OBJECT(u) FROM Position u where u.localization.id=:localizationId"
)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"LABEL", "LOCALIZATION_ID"}))
public class Position
        implements Comparable<Position>,
        Identifiable<Long>,
        DTOable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 100)
    private String label;

    private Long samples;


    private Integer routers;


    private Integer networks;


    @Column(length = 100)
    private String strongestSignal;

    @ManyToOne(fetch = FetchType.LAZY)
    private Localization localization;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SPAM_POSITION_ID")
    private PositionSpamReport spamReport;


    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public Position() {
        //JPA
    }

    public Position(String label, Localization localization) {
        this.label = label;
        this.localization = localization;
        this.networks = 0;
        this.routers = 0;
        this.samples = 0L;
        this.strongestSignal = "";
        this.spamReport = new PositionSpamReport(this);
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
    public int compareTo(Position position) {
        return this.created.compareTo(position.created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(label, position.label) &&
                Objects.equals(localization, position.localization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, localization);
    }

    public Localization getLocalization() {
        return localization;
    }

    public void incrementSamplesBy(long samples) {
        this.samples += samples;
    }

    @Override
    public DTO toDTO() {
        return new PositionDTO(id, label, samples, routers, networks, strongestSignal);
    }

    @Override
    public boolean is(Long id) {
        return this.id.equals(id);
    }

    @Override
    public Long id() {
        return id;
    }

    public void setStrongestRouter(String strongestRouter) {
        this.strongestSignal = strongestRouter;
    }

    public void setNumberOfNetworks(Integer numberOfNetworks) {
        this.networks = numberOfNetworks;
    }

    public void setNumberOfRouters(Integer numberOfRouters) {
        this.routers = numberOfRouters;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", samples=" + samples +
                ", routers=" + routers +
                ", networks=" + networks +
                ", strongestSignal='" + strongestSignal + '\'' +
                ", localization=" + localization +
                ", spamReport=" + spamReport +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}
