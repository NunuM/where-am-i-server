package me.nunum.whereami.controller;

import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.persistance.DeviceRepository;
import me.nunum.whereami.model.persistance.jpa.DeviceRepositoryJpa;
import me.nunum.whereami.model.request.UpdateDeviceRequest;

import java.security.Principal;

public class DeviceController {

    private final DeviceRepository deviceRepository;

    public DeviceController() {
        this.deviceRepository = new DeviceRepositoryJpa();
    }


    public DTO updateDevice(Principal principal, UpdateDeviceRequest request){

        final Device device = this.deviceRepository.findOrPersist(principal);

        device.setFirebaseToken(request.getFirebaseToken());

        return this.deviceRepository.save(device).toDTO();
    }

}
