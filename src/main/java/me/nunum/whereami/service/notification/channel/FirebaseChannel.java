package me.nunum.whereami.service.notification.channel;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FirebaseChannel {

    public static class FirebaseMessage {

        private final Message message;


        public FirebaseMessage(String to,
                               String title,
                               String body
        ) {
            this(to, title, body, new HashMap<>(0));
        }


        public FirebaseMessage(String to,
                               HashMap<String, String> payload
        ) {
            this(to, "", "", payload);
        }


        public FirebaseMessage(String to,
                               String title,
                               String body,
                               HashMap<String, String> payload
        ) {

            Message.Builder messageBuilder = Message.builder()
                    .putAllData(payload)
                    .setToken(to);

            if (!title.isEmpty()) {
                messageBuilder = messageBuilder.setNotification(new Notification(title, body));
            }

            this.message = messageBuilder.build();
        }
    }
}
