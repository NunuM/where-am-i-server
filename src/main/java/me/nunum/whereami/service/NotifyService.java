package me.nunum.whereami.service;

import me.nunum.whereami.model.Device;

import java.util.HashMap;
import java.util.Set;

public class NotifyService implements Runnable {

    private Set<Device> deviceSet;
    private HashMap<String, String> message;

    protected NotifyService(Set<Device> deviceSet, HashMap<String, String> message) {
        this.deviceSet = deviceSet;
        this.message = message;
    }

    @Override
    public void run() {

    }

    public static NotifyService providerDeletionNotification(final Set<Device> devices) {
        return new NotifyService(devices, new HashMap<>());
    }

}
