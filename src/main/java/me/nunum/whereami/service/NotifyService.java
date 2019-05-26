package me.nunum.whereami.service;

import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Provider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NotifyService implements Runnable {

    private Set<Device> deviceSet;
    private NotificationType notificationType;
    private HashMap<String, String> message;


    public enum NotificationType {
        EMAIL, PUSH_NOTIFICATION
    }

    protected NotifyService(Set<Device> deviceSet, HashMap<String, String> message, NotificationType notificationType) {
        this.deviceSet = deviceSet;
        this.message = message;
        this.notificationType = notificationType;
    }

    @Override
    public void run() {

    }

    public static NotifyService providerDeletionNotification(final Set<Device> devices) {
        return new NotifyService(devices, new HashMap<>(), NotificationType.PUSH_NOTIFICATION);
    }

    public static NotifyService newAlgorithmNotification(final Set<Device> devices) {
        return new NotifyService(devices, new HashMap<>(), NotificationType.PUSH_NOTIFICATION);
    }

    public static NotifyService newProviderRequest(Provider provider) {
        final HashSet<Device> devices = new HashSet<>();
        return new NotifyService(devices, new HashMap<>(), NotificationType.EMAIL);
    }

}
