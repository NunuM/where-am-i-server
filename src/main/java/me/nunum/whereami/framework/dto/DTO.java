/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.nunum.whereami.framework.dto;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * @author nuno
 */
public interface DTO {

    Map<String, Object> dtoValues();

    default Map<String, Object> dtoValues(final String timezoneID) {

        ZoneId zoneId;

        try {
            zoneId = ZoneId.of(timezoneID);
        } catch (Throwable e) {
            zoneId = ZoneId.of("UTC");
        }

        Map<String, Object> objectMap = dtoValues();

        Date created = (Date) objectMap.get("created");
        Date updated = (Date) objectMap.get("updated");

        if (created != null) {
            ZonedDateTime createdUTCTime = ZonedDateTime.ofInstant(created.toInstant(), ZoneId.of("UTC"));
            objectMap.put("created", createdUTCTime.withZoneSameInstant(zoneId).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
        }

        if (updated != null) {
            ZonedDateTime updatedUTCTime = ZonedDateTime.ofInstant(updated.toInstant(), ZoneId.of("UTC"));
            objectMap.put("updated", updatedUTCTime.withZoneSameInstant(zoneId).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
        }

        return objectMap;
    }
}
