package me.nunum.whereami.model.request;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewPredictionRequest {

    private List<FingerprintSample> samples;

    @NotNull
    private Date lastUpdate;

    @NotNull
    private Date clientNow;

    @NotNull
    private boolean onlyPolling;

    public NewPredictionRequest() {
        this(new ArrayList<>(), new Date(), new Date(), true);
    }

    public NewPredictionRequest(List<FingerprintSample> samples, Date lastUpdate, Date clientNow, boolean onlyPolling) {
        this.samples = samples;
        this.lastUpdate = lastUpdate;
        this.clientNow = clientNow;
        this.onlyPolling = onlyPolling;
    }

    public List<FingerprintSample> getSamples() {
        return samples;
    }

    public void setSamples(List<FingerprintSample> samples) {
        this.samples = samples;
    }

    public Date getLastUpdate() {

        try {
            final Duration between = Duration.between(lastUpdate.toInstant(), clientNow.toInstant());

            final Date from = Date.from(Instant.now().minus(between));

            return from;
        } catch (Exception e) {
            return lastUpdate;
        }
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isOnlyPolling() {
        return onlyPolling;
    }

    public void setOnlyPolling(boolean onlyPolling) {
        this.onlyPolling = onlyPolling;
    }

    public Date getClientNow() {
        return clientNow;
    }

    public void setClientNow(Date clientNow) {
        this.clientNow = clientNow;
    }

    @Override
    public String toString() {
        return "NewPredictionRequest{" +
                "samples=" + samples +
                ", lastUpdate=" + lastUpdate +
                ", clientNow=" + clientNow +
                ", onlyPolling=" + onlyPolling +
                '}';
    }
}
