package me.nunum.whereami.model.request;

import me.nunum.whereami.model.Fingerprint;

public class FingerprintRequest {

    private String bssid;
    private String ssid;
    private int levelDBM;
    private int centerFreq0;
    private int centerFreq1;
    private int channelWidth;
    private int frequency;
    private String timeStamp;
    private int buildid;
    private int floorid;
    private int position;
    private int localization;


    public FingerprintRequest() {
    }

    public FingerprintRequest(String bssid,
                              String ssid,
                              int levelDBM,
                              int centerFreq0,
                              int centerFreq1,
                              int channelWidth,
                              int frequency,
                              String timeStamp,
                              int buildid,
                              int floorid,
                              int position,
                              int localization) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.levelDBM = levelDBM;
        this.centerFreq0 = centerFreq0;
        this.centerFreq1 = centerFreq1;
        this.channelWidth = channelWidth;
        this.frequency = frequency;
        this.timeStamp = timeStamp;
        this.buildid = buildid;
        this.floorid = floorid;
        this.position = position;
        this.localization = localization;
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

    public int getLocalization() {
        return position;
    }

    public void setBuildid(int buildid) {
        this.buildid = buildid;
    }

    public int getFloorid() {
        return floorid;
    }

    public void setFloorid(int floorid) {
        this.floorid = floorid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public Fingerprint build() {
        return new Fingerprint(bssid, ssid, levelDBM, centerFreq0, centerFreq1, channelWidth, frequency, timeStamp, buildid, floorid, position);
    }

}
