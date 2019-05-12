package me.nunum.whereami.model.stats;

public class NetworkingStats {

    private Integer numberOfNetworks;

    private String bssid;


    public NetworkingStats() {
    }

    public NetworkingStats(Integer numberOfNetworks, String bssid) {
        this.numberOfNetworks = numberOfNetworks;
        this.bssid = bssid;
    }


    public Integer getNumberOfNetworks() {
        return numberOfNetworks;
    }

    public void setNumberOfNetworks(Integer numberOfNetworks) {
        this.numberOfNetworks = numberOfNetworks;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    @Override
    public String toString() {
        return "NetworkingStats{" +
                "numberOfNetworks=" + numberOfNetworks +
                ", bssid='" + bssid + '\'' +
                '}';
    }
}
