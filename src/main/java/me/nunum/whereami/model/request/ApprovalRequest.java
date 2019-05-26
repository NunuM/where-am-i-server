package me.nunum.whereami.model.request;

import me.nunum.whereami.model.Algorithm;

public class ApprovalRequest {

    private boolean approved;

    public ApprovalRequest() {
    }

    public ApprovalRequest(boolean approved) {
        this.approved = approved;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }


    public Algorithm updateAprroval(Algorithm algorithm) {
        algorithm.setApproved(this.approved);
        return algorithm;
    }

    @Override
    public String toString() {
        return "ApprovalRequest{" +
                "approved=" + approved +
                '}';
    }
}
