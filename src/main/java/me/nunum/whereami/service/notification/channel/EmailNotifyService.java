package me.nunum.whereami.service.notification.channel;


import me.nunum.whereami.framework.domain.Executable;
import me.nunum.whereami.utils.AppConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailNotifyService extends Executable {

    private final NewProviderMessage message;

    public EmailNotifyService(NewProviderMessage message) {
        this.message = message;
    }

    @Override
    public Boolean call() throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.host", message.getHost());
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.ssl.checkserveridentity", true);

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(System.getProperty("app.smtp.user"), System.getProperty("app.smtp.password"));
            }
        });

        session.setDebug(false);

        Transport.send(message.message(session));

        return true;
    }


    public static class NewProviderMessage {

        private final String from;
        private final String host;
        private final String to;
        private final String token;

        public NewProviderMessage(final String to, final String token) {
            this.from = AppConfig.EMAIL_FROM;
            this.host = AppConfig.EMAIL_HOST;
            this.to = to;
            this.token = token;
        }

        public String getHost() {
            return host;
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
                    ", host='" + host + '\'' +
                    ", to='" + to + '\'' +
                    ", token='" + token + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "EmailNotifyService{" +
                "message=" + message +
                '}';
    }

}
