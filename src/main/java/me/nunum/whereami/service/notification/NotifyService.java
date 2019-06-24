package me.nunum.whereami.service.notification;

import me.nunum.whereami.model.*;
import me.nunum.whereami.service.TaskManager;
import me.nunum.whereami.service.notification.channel.EmailNotifyService;
import me.nunum.whereami.service.notification.channel.FirebaseChannel;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class NotifyService {

    public static void trainingFinished(Localization localization, final Task task) {
        if (localization.getOwner().getFirebaseToken() == null
                || localization.getOwner().getFirebaseToken().isEmpty()) {
            return;
        }

        final String name = task.trainingInfo().getAlgorithm().getName();

        final HashMap<String, String> payload = new HashMap<>(4);
        payload.put("algorithmId", task.trainingInfo().getAlgorithm().getId().toString());
        payload.put("algorithmName", name);
        payload.put("localizationName", localization.getLabel());
        payload.put("algorithmProviderId", task.trainingInfo().getAlgorithmProvider().getId().toString());
        payload.put("action", TRAINED_FINISHED_NOTIFICATION_ACTION);

        TaskManager
                .getInstance()
                .queue(new FirebaseChannel(new FirebaseChannel.FirebaseMessage(
                        localization.getOwner().getFirebaseToken(),
                        TRAINED_FINISHED_NOTIFICATION_TITLE,
                        String.format(TRAINED_FINISHED_NOTIFICATION_MESSAGE, name),
                        payload
                )));
    }

    public static void providerDeletionNotification(final Set<Device> devices, final String algorithmName) {
        final TaskManager taskManager = TaskManager.getInstance();

        final HashMap<String, String> payload = new HashMap<>(2);
        payload.put("algorithmName", algorithmName);
        payload.put("action", DELETE_ALGORITHM_PROVIDER_ACTION);

        final Set<String> targets = devices
                .stream()
                .filter(e -> !(e.getFirebaseToken() == null || e.getFirebaseToken().isEmpty()))
                .map(Device::getFirebaseToken)
                .collect(Collectors.toSet());

        taskManager
                .queue(new FirebaseChannel(new FirebaseChannel.FirebaseMessage(
                        targets,
                        DELETE_ALGORITHM_PROVIDER_TITLE,
                        DELETE_ALGORITHM_PROVIDER_BODY,
                        payload
                )));
    }

    public static void newAlgorithmNotification(final Set<Device> devices, final Algorithm algorithm) {
        final TaskManager taskManager = TaskManager.getInstance();

        final Set<String> targets = devices
                .stream()
                .filter(e -> !(e.getFirebaseToken() == null || e.getFirebaseToken().isEmpty()))
                .map(Device::getFirebaseToken)
                .collect(Collectors.toSet());

        final HashMap<String, String> payload = new HashMap<>(1);
        payload.put("action", NEW_ALGORITHM_NOTIFICATION_ACTION);
        payload.put("algorithmId", algorithm.getId().toString());
        payload.put("algorithmName", algorithm.getName());

        taskManager
                .queue(new FirebaseChannel(new FirebaseChannel
                        .FirebaseMessage(targets,
                        NEW_ALGORITHM_NOTIFICATION_TITLE,
                        NEW_ALGORITHM_NOTIFICATION_BODY,
                        payload)));

    }

    public static void newProviderRequest(final Provider provider) {
        TaskManager
                .getInstance()
                .queue(new EmailNotifyService(new EmailNotifyService.NewProviderMessage(provider.getEmail(), provider.getToken())));
    }


    private static final String TRAINED_FINISHED_NOTIFICATION_TITLE = "Training was finished";
    private static final String TRAINED_FINISHED_NOTIFICATION_ACTION = "2";
    private static final String TRAINED_FINISHED_NOTIFICATION_MESSAGE = "A new model for the algorithm %s was finished";


    private static final String NEW_ALGORITHM_NOTIFICATION_TITLE = "New Algorithm";
    private static final String NEW_ALGORITHM_NOTIFICATION_ACTION = "1";
    private static final String NEW_ALGORITHM_NOTIFICATION_BODY = "A Algorithm is waiting for your approval";

    private static final String DELETE_ALGORITHM_PROVIDER_TITLE = "Algorithm provider deletion";
    private static final String DELETE_ALGORITHM_PROVIDER_BODY = "A provider for the algorithm %s was deleted and your model was affected as well";
    private static final String DELETE_ALGORITHM_PROVIDER_ACTION = "3";

}
