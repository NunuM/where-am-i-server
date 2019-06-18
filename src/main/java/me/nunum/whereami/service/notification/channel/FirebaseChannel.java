package me.nunum.whereami.service.notification.channel;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import me.nunum.whereami.framework.domain.Executable;
import me.nunum.whereami.utils.AppConfig;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FirebaseChannel extends Executable {

    private static final Logger LOGGER = Logger.getLogger(FirebaseChannel.class.getSimpleName());

    private final FirebaseMessage message;

    public FirebaseChannel(FirebaseMessage message) {
        this.message = message;
    }

    @Override
    public Boolean call() throws Exception {

        LOGGER.log(Level.INFO, "Task + " + super.toString() + " - Sending: {0}", this.message);
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance(AppConfig.getInstance().firebaseApp());
        firebaseMessaging.sendMulticast(this.message.getMessage());
        return true;
    }


    public static class FirebaseMessage {

        private final Set<String> recipients;
        private final String title;
        private final String body;
        private final HashMap<String, String> payload;

        public FirebaseMessage(String to,
                               String title,
                               String body
        ) {
            this(new HashSet<>(Collections.singletonList(to)), title, body, new HashMap<>(0));
        }


        public FirebaseMessage(String to,
                               HashMap<String, String> payload
        ) {
            this(new HashSet<>(Collections.singletonList(to)), "", "", payload);
        }

        public FirebaseMessage(Set<String> recipients,
                               String title,
                               String body
        ) {
            this(recipients, title, body, new HashMap<>(0));
        }


        public FirebaseMessage(String to,
                               String title,
                               String body,
                               HashMap<String, String> payload
        ) {
            this(new HashSet<>(Collections.singletonList(to)), title, body, payload);
        }


        public FirebaseMessage(Set<String> recipients,
                               String title,
                               String body,
                               HashMap<String, String> payload
        ) {
            this.title = title;
            this.body = body;
            this.recipients = recipients;
            this.payload = payload;
        }

        public MulticastMessage getMessage() {

            MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                    .putAllData(payload)
                    .addAllTokens(new ArrayList<>(recipients));

            if (!title.isEmpty()) {
                messageBuilder = messageBuilder.setNotification(new Notification(title, body));
            }

            return messageBuilder.build();
        }

        @Override
        public String toString() {
            return "FirebaseMessage{" +
                    "recipients=" + recipients +
                    ", title='" + title + '\'' +
                    ", body='" + body + '\'' +
                    ", payload=" + payload +
                    '}';
        }
    }
}
