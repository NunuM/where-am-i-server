package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.ProviderDTO;
import org.eclipse.persistence.annotations.Index;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "Provider.findByToken",
                query = "SELECT OBJECT(u) FROM Provider u WHERE u.token=:token"
        ),
        @NamedQuery(
                name = "Provider.findByDevice",
                query = "SELECT OBJECT(u) FROM Provider u WHERE u.requester.id=:deviceId"
        ),
        @NamedQuery(
                name = "Provider.findByEmail",
                query = "SELECT OBJECT(u) FROM Provider u WHERE u.email=:email"
        )
})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"REQUESTER_ID"}))
public class Provider implements DTOable {

    @Id
    @GeneratedValue
    private Long id;

    @Index(unique = true)
    private String email;


    @Index(unique = true)
    private String token;


    private boolean isConfirmed;


    @OneToOne
    @JoinColumn(name = "REQUESTER_ID")
    private Device requester;


    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;


    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmed;


    protected Provider() {
    }

    public Provider(String email, String token, boolean isConfirmed, Device requester) {
        this.email = email;
        this.token = token;
        this.isConfirmed = isConfirmed;
        this.requester = requester;
        this.onCreate();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public Device getRequester() {
        return requester;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    public Date getConfirmed() {
        return confirmed;
    }

    public void providerHasConfirmedEmail() {
        this.confirmed = Date.from(Instant.now());
        this.isConfirmed = true;
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
        Provider provider = (Provider) o;
        return Objects.equals(requester, provider.requester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requester);
    }


    @Override
    public DTO toDTO() {
        return new ProviderDTO(id, email, isConfirmed);
    }
}
