package me.nunum.whereami.model.request;

import me.nunum.whereami.model.Fingerprint;

import java.util.Objects;

public class FingerprintRequest {

    private String bssid;
    private String ssid;
    private int levelDBM;
    private int centerFreq0;
    private int centerFreq1;
    private int channelWidth;
    private int frequency;
    private String timeStamp;
    private int buildId;
    private int floorId;
    private Long positionId;
    private Long localizationId;


    public FingerprintRequest() {
        this("", "", 0, 0, 0, 0, 0, "", 0, 0, 0, 0);
    }

    public FingerprintRequest(String bssid,
                              String ssid,
                              int levelDBM,
                              int centerFreq0,
                              int centerFreq1,
                              int channelWidth,
                              int frequency,
                              String timeStamp,
                              int buildId,
                              int floorId,
                              long position,
                              long localization) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.levelDBM = levelDBM;
        this.centerFreq0 = centerFreq0;
        this.centerFreq1 = centerFreq1;
        this.channelWidth = channelWidth;
        this.frequency = frequency;
        this.timeStamp = timeStamp;
        this.buildId = buildId;
        this.floorId = floorId;
        this.positionId = position;
        this.localizationId = localization;
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

    public long getLocalizationId() {
        return localizationId;
    }

    public void setLocalizationId(Long localizationId) {
        this.localizationId = localizationId;
    }

    public void setBuildId(int buildId) {
        this.buildId = buildId;
    }

    public int getFloorId() {
        return floorId;
    }

    public void setFloorId(int floorId) {
        this.floorId = floorId;
    }

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FingerprintRequest that = (FingerprintRequest) o;
        return Objects.equals(positionId, that.positionId) &&
                Objects.equals(localizationId, that.localizationId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(positionId, localizationId);
    }

    public Fingerprint build() {
        return new Fingerprint(bssid, ssid, levelDBM, centerFreq0, centerFreq1, channelWidth, frequency, timeStamp, buildId, floorId, positionId, localizationId);
    }

}
