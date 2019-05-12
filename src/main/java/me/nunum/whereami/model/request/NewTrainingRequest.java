package me.nunum.whereami.model.request;

import javax.validation.constraints.NotNull;

public class NewTrainingRequest {

    @NotNull
    private Long algorithmId;


    public NewTrainingRequest() {
        this(0L);
    }

    public NewTrainingRequest(Long algorithmId) {
        this.algorithmId = algorithmId;

    }

    public Long getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(Long algorithmId) {
        this.algorithmId = algorithmId;
    }

    @Override
    public String toString() {
        return "NewTrainingRequest{" +
                "algorithmId=" + algorithmId +
                '}';
    }
}
