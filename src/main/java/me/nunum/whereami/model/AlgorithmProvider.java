package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.AlgorithmProviderDTO;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "method"}))
public class AlgorithmProvider implements DTOable {

    public static final String HTTP_PROVIDER_INGESTION_URL_KEY = "url_to_receive_data";
    public static final String HTTP_PROVIDER_PREDICTION_URL_KEY = "url_to_predict";

    @Id
    @GeneratedValue
    private Long id;


    private String email;


    private METHOD method;


    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;


    @ElementCollection
    private Map<String, String> properties;

    public enum METHOD {

        HTTP {
            @Override
            public String toString() {
                return "HTTP";
            }

            @Override
            public String[] requiredKeys() {
                return new String[]{HTTP_PROVIDER_INGESTION_URL_KEY, HTTP_PROVIDER_PREDICTION_URL_KEY};
            }
        },
        GIT {
            @Override
            public String toString() {
                return "GIT";
            }

            @Override
            public String[] requiredKeys() {
                return new String[]{"repository_url"};
            }
        },
        UNSUPPORTED {
            @Override
            public String[] requiredKeys() {
                return new String[0];
            }
        };


        public abstract String[] requiredKeys();


        public static METHOD parse(String method) {
            for (METHOD m : METHOD.values()) {
                if (m.toString().toLowerCase().equalsIgnoreCase(method.toLowerCase())) {
                    return m;
                }
            }

            return METHOD.UNSUPPORTED;
        }
    }

    public AlgorithmProvider() {
    }

    public AlgorithmProvider(String email, METHOD method, Map<String, String> properties) {
        this.email = email;
        this.method = method;
        this.properties = properties;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public METHOD getMethod() {
        return method;
    }

    public Map<String, String> getProperties() {
        return properties;
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
    public DTO toDTO() {
        return new AlgorithmProviderDTO(this.id, this.email, this.method, this.properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlgorithmProvider that = (AlgorithmProvider) o;
        return Objects.equals(email, that.email) &&
                method == that.method;
    }

    @Override
    public int hashCode() {

        return Objects.hash(email, method);
    }

    @Override
    public String toString() {
        return "AlgorithmProvider{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", method=" + method +
                ", created=" + created +
                ", updated=" + updated +
                ", properties=" + properties +
                '}';
    }
}
