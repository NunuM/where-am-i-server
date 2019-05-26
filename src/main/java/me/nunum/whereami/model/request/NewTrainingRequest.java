package me.nunum.whereami.model.request;

import javax.validation.constraints.NotNull;

public class NewTrainingRequest {

    @NotNull
    private Long algorithmId;

    @NotNull
    private Long providerId;


    public NewTrainingRequest() {
        this(0L, 0L);
    }

    public NewTrainingRequest(Long algorithmId, Long provider) {
        this.algorithmId = algorithmId;
        this.providerId = provider;
    }

    public Long getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(Long algorithmId) {
        this.algorithmId = algorithmId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long provider) {
        this.providerId = provider;
    }

    @Override
    public String toString() {
        return "NewTrainingRequest{" +
                "algorithmId=" + algorithmId +
                ", provider=" + providerId +
                '}';
    }
}
