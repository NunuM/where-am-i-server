package me.nunum.whereami.model.request;

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

    public void updateFeebdack(Prediction prediction) {
        if (this.wasCorrect) {
            prediction.correctPrediction();
        } else {
            prediction.incorrectPrediction();
        }
    }

    @Override
    public String toString() {
        return "UpdatePredictionRequest{" +
                "wasCorrect=" + wasCorrect +
                '}';
    }
}
