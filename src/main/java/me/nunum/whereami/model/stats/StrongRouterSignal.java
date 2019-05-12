package me.nunum.whereami.model.stats;

public class StrongRouterSignal {

    private int levelDvm;

    private String ssid;

    public StrongRouterSignal() {
    }

    public StrongRouterSignal(int levelDvm, String ssid) {
        this.levelDvm = levelDvm;
        this.ssid = ssid;
    }

    public int getLevelDvm() {
        return levelDvm;
    }

    public void setLevelDvm(int levelDvm) {
        this.levelDvm = levelDvm;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    @Override
    public String toString() {
        return "StrongRouterSignal{" +
                "levelDvm=" + levelDvm +
                ", ssid='" + ssid + '\'' +
                '}';
    }
}
