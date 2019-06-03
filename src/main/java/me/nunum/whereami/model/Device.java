package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "Device.findByInstance",
                query = "SELECT OBJECT(u) FROM Device u where u.instanceId=:instance"
        ),
        @NamedQuery(
                name = "Device.findAllDevicesInRole",
                query = "SELECT OBJECT(u) FROM Device u JOIN Role r where r.name=:role "
        )
})
public class Device
        implements Comparable<Device> , DTOable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, updatable = false, nullable = false)
    private String instanceId;

    private String firebaseToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @ManyToMany(mappedBy = "devices")
    private List<Role> roles;

    protected Device() {
        //JPA
    }

    public Device(String instanceId) {
        this(instanceId,"", new ArrayList<>(0));
    }

    public Device(String instanceId, String firebaseToken) {
        this(instanceId,firebaseToken, new ArrayList<>(0));
    }

    public Device(String instanceId, String firebaseToken, List<Role> roles){
        this.instanceId = instanceId;
        this.firebaseToken = firebaseToken;
        this.roles = roles;

    }

    public Device(String instanceId, List<Role> roles) {
        this.instanceId = instanceId;
        this.roles = roles;
        this.onCreate();
    }

    public Long getId() {
        return id;
    }

    public String instanceId() {
        return instanceId;
    }


    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
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
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", instanceId='" + instanceId + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    @Override
    public int compareTo(Device device) {
        return this.created.compareTo(device.created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Device)) return false;

        Device device = (Device) o;

        return this.instanceId().equals(device.instanceId());
    }

    @Override
    public int hashCode() {
        return instanceId.hashCode();
    }

    public boolean isInRole(final String role) {
        return this.roles.stream().anyMatch(e -> e.is(role));
    }

    @Override
    public DTO toDTO() {
        return null;
    }
}
