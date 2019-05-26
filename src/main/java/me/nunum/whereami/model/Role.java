package me.nunum.whereami.model;

import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ROLE")
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    @Index(unique = true)
    private String role;

    @ManyToMany
    private List<Device> devices;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    protected Role() {
    }

    public Role(String role) {
        this.role = role;
        this.devices = new ArrayList<>();
    }

    public void addDevice(final Device device) {
        this.devices.add(device);
    }

    public boolean is(String role) {
        return this.role.equalsIgnoreCase(role);
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
        Role roles = (Role) o;
        return role.equals(roles.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role='" + role + '\'' +
                ", devices=" + devices +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}
