package me.nunum.whereami.model;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.framework.dto.DTOable;
import me.nunum.whereami.model.dto.PredictionDTO;
import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "Prediction.maxRequestId",
                query = "SELECT MAX (u.requestId) FROM Prediction u WHERE u.localizationId=:localizationId"
        ),
        @NamedQuery(
                name = "Prediction.allPredictionsSince",
                query = "SELECT OBJECT (u) FROM Prediction u WHERE u.localizationId=:localizationId AND u.created > :since"
        )
})
public class Prediction implements DTOable {

    @Id
    @GeneratedValue
    private Long id;

    private Long requestId;

    @Index
    private Long localizationId;

    private Long positionId;

    private Float accuracy;

    private Long providerId;

    private String positionLabel;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;


    protected Prediction() {
    }

    /**
     * @param requestId
     * @param localizationId
     * @param positionId
     * @param accuracy
     */
    public Prediction(Long requestId, Long localizationId, Long positionId, String positionLabel, Float accuracy, Long providerId) {
        this.requestId = requestId;
        this.localizationId = localizationId;
        this.positionId = positionId;
        this.accuracy = accuracy;
        this.providerId = providerId;
        this.positionLabel = positionLabel;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    @PrePersist
    protected void onCreate() {
        created = Date.from(Instant.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prediction that = (Prediction) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }


    @Override
    public DTO toDTO() {
        return new PredictionDTO(this.id,this.localizationId,this.positionLabel,this.requestId, this.positionId, this.accuracy, this.providerId);
    }
}
