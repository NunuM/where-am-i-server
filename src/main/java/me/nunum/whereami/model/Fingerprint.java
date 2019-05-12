package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class Fingerprint
        implements Comparable<Fingerprint>,
        DTOable {

    @Id
    @GeneratedValue
    private Long id;

    private String uid;

    private String bssid;

    private String ssid;

    private Integer levelDBM;

    private Integer centerFreq0;

    private Integer centerFreq1;

    private Integer channelWidth;

    private Integer frequency;

    private String timeStamp;

    private Integer localizationId;

    private Integer floorid;

    private Integer positionId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;


    protected Fingerprint() {
        //JPA
    }

    public Fingerprint(String bssid,
                       String ssid,
                       Integer levelDBM,
                       Integer centerFreq0,
                       Integer centerFreq1,
                       Integer channelWidth,
                       Integer frequency,
                       String timeStamp,
                       Integer buildid,
                       Integer floorid,
                       Integer position) {

        this.uid = UUID.randomUUID().toString();
        this.bssid = bssid;
        this.ssid = ssid;
        this.levelDBM = levelDBM;
        this.centerFreq0 = centerFreq0;
        this.centerFreq1 = centerFreq1;
        this.channelWidth = channelWidth;
        this.frequency = frequency;
        this.timeStamp = timeStamp;
        this.localizationId = buildid;
        this.floorid = floorid;
        this.positionId = position;
    }


    @Override
    public int compareTo(Fingerprint fingerprint) {
        return this.created.compareTo(fingerprint.created);
    }

    @Override
    public DTO toDTO() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fingerprint)) return false;

        Fingerprint that = (Fingerprint) o;

        if (!uid.equals(that.uid)) return false;
        return created.equals(that.created);
    }

    @Override
    public int hashCode() {
        int result = uid.hashCode();
        result = 31 * result + created.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Fingerprint{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", bssid='" + bssid + '\'' +
                ", ssid='" + ssid + '\'' +
                ", levelDBM=" + levelDBM +
                ", centerFreq0=" + centerFreq0 +
                ", centerFreq1=" + centerFreq1 +
                ", channelWidth=" + channelWidth +
                ", frequency=" + frequency +
                ", timeStamp='" + timeStamp + '\'' +
                ", localizationId=" + localizationId +
                ", floorid=" + floorid +
                ", positionId=" + positionId +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    @PrePersist
    protected void onCreate() {
        updated = created = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date(System.currentTimeMillis());
    }
}
