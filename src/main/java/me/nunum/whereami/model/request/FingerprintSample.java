package me.nunum.whereami.model.request;

import java.util.HashMap;

public class FingerprintSample {

    private String bssid;
    private String ssid;
    private int levelDBM;
    private int centerFreq0;
    private int centerFreq1;
    private int channelWidth;
    private int frequency;
    private String timeStamp;


    public FingerprintSample() {
        this("", "", 0, 0, 0, 0, 0, "");
    }


    public FingerprintSample(String bssid, String ssid, int levelDBM, int centerFreq0, int centerFreq1, int channelWidth, int frequency, String timeStamp) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.levelDBM = levelDBM;
        this.centerFreq0 = centerFreq0;
        this.centerFreq1 = centerFreq1;
        this.channelWidth = channelWidth;
        this.frequency = frequency;
        this.timeStamp = timeStamp;
    }


    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getLevelDBM() {
        return levelDBM;
    }

    public void setLevelDBM(int levelDBM) {
        this.levelDBM = levelDBM;
    }

    public int getCenterFreq0() {
        return centerFreq0;
    }

    public void setCenterFreq0(int centerFreq0) {
        this.centerFreq0 = centerFreq0;
    }

    public int getCenterFreq1() {
        return centerFreq1;
    }

    public void setCenterFreq1(int centerFreq1) {
        this.centerFreq1 = centerFreq1;
    }

    public int getChannelWidth() {
        return channelWidth;
    }

    public void setChannelWidth(int channelWidth) {
        this.channelWidth = channelWidth;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public HashMap<String, Object> values(){
        final HashMap<String, Object> map = new HashMap<>(7);

        map.put("bssid", bssid);
        map.put("ssid", ssid);
        map.put("levelDBM",levelDBM );
        map.put("centerFreq0",centerFreq0 );
        map.put("centerFreq1",centerFreq1 );
        map.put("channelWidth",channelWidth);
        map.put("frequency",frequency);
        map.put("timeStamp", timeStamp);

        return map;
    }
}
