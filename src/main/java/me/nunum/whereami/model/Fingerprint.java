package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.FingerprintDTO;
import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@Entity
@XmlRootElement(name = "fingerprint")
@XmlAccessorType(XmlAccessType.FIELD)
public class Fingerprint
        implements Comparable<Fingerprint>,
        DTOable {

    @Id
    @GeneratedValue
    private Long id;

    @XmlElement
    private String uid;

    private String bssid;

    private String ssid;

    private Integer levelDBM;

    private Integer centerFreq0;

    private Integer centerFreq1;

    private Integer channelWidth;

    private Integer frequency;

    private String timeStamp;

    @Index
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public Integer getLevelDBM() {
        return levelDBM;
    }

    public void setLevelDBM(Integer levelDBM) {
        this.levelDBM = levelDBM;
    }

    public Integer getCenterFreq0() {
        return centerFreq0;
    }

    public void setCenterFreq0(Integer centerFreq0) {
        this.centerFreq0 = centerFreq0;
    }

    public Integer getCenterFreq1() {
        return centerFreq1;
    }

    public void setCenterFreq1(Integer centerFreq1) {
        this.centerFreq1 = centerFreq1;
    }

    public Integer getChannelWidth() {
        return channelWidth;
    }

    public void setChannelWidth(Integer channelWidth) {
        this.channelWidth = channelWidth;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getLocalizationId() {
        return localizationId;
    }

    public void setLocalizationId(Integer localizationId) {
        this.localizationId = localizationId;
    }

    public Integer getFloorid() {
        return floorid;
    }

    public void setFloorid(Integer floorid) {
        this.floorid = floorid;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
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

    @Override
    public int compareTo(Fingerprint fingerprint) {
        return this.created.compareTo(fingerprint.created);
    }

    @Override
    public DTO toDTO() {
        return new FingerprintDTO(this);
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
