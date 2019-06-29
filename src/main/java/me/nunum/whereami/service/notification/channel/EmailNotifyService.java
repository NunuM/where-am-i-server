package me.nunum.whereami.service.notification.channel;


import me.nunum.whereami.framework.domain.Executable;
import me.nunum.whereami.framework.dto.DTO;
import me.nunum.whereami.model.Feedback;
import me.nunum.whereami.model.Task;
import me.nunum.whereami.utils.AppConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailNotifyService extends Executable {

    private final EmailMessage message;

    public EmailNotifyService(EmailMessage message) {
        this.message = message;
    }

    @Override
    public Boolean call() throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.host", AppConfig.EMAIL_HOST);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.ssl.checkserveridentity", "false");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(System.getProperty("app.smtp.user"), System.getProperty("app.smtp.password"));
            }
        });

        Transport.send(message.message(session));

        return true;
    }

    @Override
    public String toString() {
        return "EmailNotifyService{" +
                "message=" + message +
                '}';
    }


    interface EmailMessage {
        MimeMessage message(Session session) throws MessagingException;
    }

    public static class NewProviderMessage implements EmailMessage {

        private final String from;
        private final String to;
        private final String token;

        public NewProviderMessage(final String to, final String token) {
            this.from = AppConfig.EMAIL_FROM;
            this.to = to;
            this.token = token;
        }

        public MimeMessage message(Session session) throws MessagingException {

            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};

            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("Provider email confirmation");
            msg.setSentDate(new Date());

            msg.setContent(String.format("Hi,%n%nTanks for your registration. You can complete by clicking https://whereami.nunum.me/provider?token=%s", token), "text/plain");

            return msg;
        }

        @Override
        public String toString() {
            return "NewProviderMessage{" +
                    "from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    ", token='" + token + '\'' +
                    '}';
        }
    }

    public static class NewTrainingRequest
            implements EmailMessage {

        private final DTO providerInfo;
        private final DTO taskInfo;

        public NewTrainingRequest(final Task task) {
            this.taskInfo = task.toDTO();
            this.providerInfo = task.trainingInfo().getAlgorithmProvider().toDTO();
        }

        @Override
        public MimeMessage message(Session session) throws MessagingException {
            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(AppConfig.EMAIL_FROM));
            InternetAddress[] address = {new InternetAddress(AppConfig.EMAIL_ADMIN_CONTACT)};

            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("New Training request");
            msg.setSentDate(new Date());

            msg.setContent(String.format("The following provider %s was issued the task %s",
                    providerInfo.dtoValues(), taskInfo.dtoValues()), "text/plain");

            return msg;
        }
    }


    public static class NewFeedback
            implements EmailMessage {

        private final DTO info;

        public NewFeedback(final Feedback feedback) {
            this.info = feedback.toDTO();
        }

        @Override
        public MimeMessage message(Session session) throws MessagingException {
            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(AppConfig.EMAIL_FROM));
            InternetAddress[] address = {new InternetAddress(AppConfig.EMAIL_ADMIN_CONTACT)};

            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("New Feedback");
            msg.setSentDate(new Date());

            msg.setContent(String.format("A new feedback was received: %s", this.info), "text/plain");

            return msg;
        }
    }

}
