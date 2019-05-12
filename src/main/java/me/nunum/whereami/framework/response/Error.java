package me.nunum.whereami.framework.response;

public class Error {

    private String reason;

    public Error() {
    }

    public Error(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Error{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
