package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.LocalizationReportDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NamedQuery(
        name = "LocalizationSpam.findByLocalizationId",
        query = "SELECT OBJECT(u) FROM LocalizationSpamReport u where u.localization.id=:localizationId"
)
public class LocalizationSpamReport
        implements Comparable<LocalizationSpamReport>,
        DTOable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private List<Device> reporters;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "spamReport")
    private Localization localization;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public LocalizationSpamReport() {
    }

    public LocalizationSpamReport(Localization localization) {
        this.reporters = new ArrayList<>();
        this.localization = localization;
    }

    public boolean newReport(Device device) {
        return this.reporters.add(device);
    }

    public List<Device> getReporters() {
        return reporters;
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
    public int compareTo(LocalizationSpamReport localizationSpamReport) {
        return this.created.compareTo(localizationSpamReport.created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalizationSpamReport)) return false;

        LocalizationSpamReport that = (LocalizationSpamReport) o;

        if (!localization.equals(that.localization)) return false;
        return created.equals(that.created);
    }

    @Override
    public int hashCode() {
        int result = localization.hashCode();
        result = 31 * result + created.hashCode();
        return result;
    }


    @Override
    public DTO toDTO() {
        return new LocalizationReportDTO(this.id, this.reporters.size());
    }
}
