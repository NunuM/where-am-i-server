package me.nunum.whereami.model;

import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@NamedQuery(name = "Role.findByRoleName", query = "SELECT OBJECT (u) FROM Role u WHERE u.name=:role")
public class Role {

    public static final String ADMIN = "admin";
    public static final String PROVIDER = "provider";

    @Id
    @GeneratedValue
    private Long id;

    @Index(unique = true)
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Device> devices;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    protected Role() {
    }

    public Role(String role) {
        this.name = role;
        this.devices = new ArrayList<>();
    }

    public void addDevice(final Device device) {
        this.devices.add(device);
    }

    public boolean is(String role) {
        return this.name.equalsIgnoreCase(role);
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
        return name.equals(roles.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", devices=" + devices +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }
}
