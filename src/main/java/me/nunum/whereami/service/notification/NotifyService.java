package me.nunum.whereami.service.notification;

import me.nunum.whereami.model.Device;
import me.nunum.whereami.model.Provider;
import me.nunum.whereami.service.notification.channel.EmailNotifyService;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotifyService {

    private static final Logger LOGGER = Logger.getLogger(NotifyService.class.getSimpleName());

    private final Callable<Boolean> callableService;

    private NotifyService(Callable<Boolean> callableService) {
        this.callableService = callableService;
    }

    public void run() {
        try {
            LOGGER.log(Level.INFO, "Sending notification: " + callableService);
            Boolean succeeded = this.callableService.call();
            LOGGER.log(Level.INFO, "Notification returned: " + succeeded);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not sent notification", e);
        }
    }

    public static NotifyService providerDeletionNotification(final Set<Device> devices) {
        return new NotifyService(() -> true);
    }

    public static NotifyService newAlgorithmNotification(final Set<Device> devices) {
        return new NotifyService(() -> true);
    }

    public static NotifyService newProviderRequest(final Provider provider) {
        return new NotifyService(new EmailNotifyService(new EmailNotifyService.NewProviderMessage(provider.getEmail(), provider.getToken())));
    }

}
