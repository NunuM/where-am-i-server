package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.AlgorithmProviderDTO;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"provider_id", "method", "ALG_OWNER_ID"}))
public class AlgorithmProvider implements DTOable {

    public static final String HTTP_PROVIDER_INGESTION_URL_KEY = "url_to_receive_data";
    public static final String HTTP_PROVIDER_PREDICTION_URL_KEY = "url_to_predict";

    public static final String GIT_PROVIDER_URL_KEY = "repository_url";

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Provider provider;


    private METHOD method;

    private int successPredictions;

    private int failurePredictions;

    private boolean isDeployed;


    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;


    @ElementCollection
    private Map<String, String> properties;

    public boolean belongs(final Device device) {
        return this.provider.getRequester().equals(device);
    }


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
                return new String[]{GIT_PROVIDER_URL_KEY};
            }

            @Override
            public boolean isAutomaticDeployed() {
                return false;
            }
        },
        UNSUPPORTED {
            @Override
            public String[] requiredKeys() {
                return new String[0];
            }
        };


        public abstract String[] requiredKeys();

        public boolean isAutomaticDeployed() {
            return true;
        }


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

    public AlgorithmProvider(Provider provider, METHOD method, Map<String, String> properties) {
        this.provider = provider;
        this.method = method;
        this.properties = properties;
        this.failurePredictions = 0;
        this.successPredictions = 0;
        this.isDeployed = method.isAutomaticDeployed();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return this.provider.getEmail();
    }

    public METHOD getMethod() {
        return method;
    }

    public void setMethod(METHOD method) {
        this.method = method;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public boolean wasVerified() {
        return this.provider.isConfirmed();
    }

    public boolean isDeployed() {
        return isDeployed;
    }

    public void setDeployed(boolean deployed) {
        isDeployed = deployed;
    }

    public float getPredictionRate() {
        float result = 0f;
        int sum = this.failurePredictions + this.successPredictions;

        if (sum > 0) {
            result = (successPredictions / (float) sum) * 100;
        }
        return result;
    }

    public void incrementSuccessPredictions() {
        this.successPredictions += 1;
    }

    public void incrementFailurePerfections() {
        this.failurePredictions += 1;
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
        return new AlgorithmProviderDTO(this.id, this.provider.getEmail(), this.method, this.properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlgorithmProvider that = (AlgorithmProvider) o;
        return Objects.equals(this.provider.getEmail(), that.provider.getEmail()) &&
                method == that.method;
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.provider.getEmail(), method);
    }

}
