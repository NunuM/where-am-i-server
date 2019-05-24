package me.nunum.whereami.model.request;

import javax.validation.constraints.NotNull;

public class NewTrainingRequest {

    @NotNull
    private Long algorithmId;

    @NotNull
    private Long provider;


    public NewTrainingRequest() {
        this(0L, 0L);
    }

    public NewTrainingRequest(Long algorithmId, Long provider) {
        this.algorithmId = algorithmId;
        this.provider = provider;
    }

    public Long getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(Long algorithmId) {
        this.algorithmId = algorithmId;
    }

    public Long getProvider() {
        return provider;
    }

    public void setProvider(Long provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "NewTrainingRequest{" +
                "algorithmId=" + algorithmId +
                ", provider=" + provider +
                '}';
    }
}
