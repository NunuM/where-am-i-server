package me.nunum.whereami.model.request;


import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class NewAlgorithmRunRequest {


    @NotNull
    private Long localizationId;

    @NotNull
    private List<FingerprintSample> samples;

    public NewAlgorithmRunRequest(){
        this(0L, new ArrayList<>());
    }


    public NewAlgorithmRunRequest(Long localizationId, List<FingerprintSample> samples) {
        this.localizationId = localizationId;
        this.samples = samples;
    }

    public Long getLocalizationId() {
        return localizationId;
    }

    public void setLocalizationId(Long localizationId) {
        this.localizationId = localizationId;
    }

    public List<FingerprintSample> getSamples() {
        return samples;
    }

    public void setSamples(List<FingerprintSample> samples) {
        this.samples = samples;
    }


    @Override
    public String toString() {
        return "NewAlgorithmRunRequest{" +
                "localizationId=" + localizationId +
                ", samples=" + samples +
                '}';
    }
}
