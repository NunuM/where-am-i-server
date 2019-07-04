package me.nunum.whereami.model.request;

import me.nunum.whereami.model.AlgorithmProvider;
import me.nunum.whereami.model.Prediction;

public class UpdatePredictionRequest {

    private boolean wasCorrect;

    public UpdatePredictionRequest() {
        this(false);
    }

    public UpdatePredictionRequest(boolean wasCorrect) {
        this.wasCorrect = wasCorrect;
    }

    public boolean isWasCorrect() {
        return wasCorrect;
    }

    public void setWasCorrect(boolean wasCorrect) {
        this.wasCorrect = wasCorrect;
    }

    public void updateFeedback(Prediction prediction) {
        if (this.wasCorrect) {
            prediction.correctPrediction();
        } else {
            prediction.incorrectPrediction();
        }
    }

    public void updateAlgorithmProviderStats(AlgorithmProvider provider) {
        if (this.wasCorrect) {
            provider.incrementSuccessPredictions();
        } else {
            provider.incrementFailurePrections();
        }
    }

    @Override
    public String toString() {
        return "UpdatePredictionRequest{" +
                "wasCorrect=" + wasCorrect +
                '}';
    }
}
