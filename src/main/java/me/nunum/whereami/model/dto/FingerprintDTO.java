package me.nunum.whereami.model.dto;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Fingerprint;

import java.util.HashMap;
import java.util.Map;

public class FingerprintDTO implements DTO {

    private HashMap<String,Object> map;

    public FingerprintDTO(Fingerprint fingerprint) {

        map = new HashMap<>(11);

        map.put("id" , fingerprint.getId());
        map.put("uid" , fingerprint.getUid());
        map.put("bssid" , fingerprint.getBssid());
        map.put("ssid" , fingerprint.getBssid());
        map.put("levelDBM" , fingerprint.getLevelDBM());
        map.put("centerFreq0" , fingerprint.getCenterFreq0());
        map.put("centerFreq1" , fingerprint.getCenterFreq1());
        map.put("channelWidth", fingerprint.getChannelWidth());
        map.put("frequency", fingerprint.getFrequency());
        map.put("timeStamp", fingerprint.getTimeStamp());
        map.put("localizationId", fingerprint.getLocalizationId());
        map.put("positionId", fingerprint.getPositionId());

    }

    @Override
    public Map<String, Object> dtoValues() {
        return map;
    }
}
