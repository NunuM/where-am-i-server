package me.nunum.whereami.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import me.nunum.whereami.framework.interceptor.ClientLoggingInterceptor;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.glassfish.jersey.client.ClientConfig;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class AppConfig {

    private static final AppConfig ourInstance = new AppConfig();

    private FirebaseApp firebaseApp;
    private final ClientConfig clientConfig;

    private AppConfig() {
        clientConfig = new ClientConfig();
        clientConfig.register(ClientLoggingInterceptor.class);
    }

    public static final String APP_NAME = "WhereAmI";
    public static final String APP_DESCRIPTION = "This is a free, collaborative platform with the goal of helping researchers to test and develop indoor tracking algorithms using Wi-Fi signal information";
    public static final String APP_LICENSE = "https://github.com/NunuM/where-am-i-server/blob/master/LICENSE";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_PORT = System.getProperty("app.server.port", "8080");

    public static final String APP_AUTHORITY = String.format("%s:%s", System.getProperty("app.domain", "localhost"), APP_PORT);
    public static final String APP_API_PATH = "api";

    public static final String JPA_UNIT = System.getProperty("app.persistence.unit", "me.nunum.whereami.JPA_PERSISTENCE");

    public static final String X_APP_HEADER = "X-APP";

    public static final String EMAIL_HOST = System.getProperty("app.smtp.host", "");
    public static final String EMAIL_FROM = System.getProperty("app.no.reply.email", "no-reply@whereami.nunum.me");
    public static final String EMAIL_ADMIN_CONTACT = System.getProperty("app.admin.email", EMAIL_FROM);


    /**
     * @return See {@link FirebaseApp}
     * @throws FileNotFoundException
     * @throws IOException
     */
    public synchronized FirebaseApp firebaseApp() throws IOException {
        if (this.firebaseApp == null) {
            FileInputStream serviceAccount =
                    new FileInputStream(System.getProperty("app.firebase.service.account"));

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://where-am-i-d215e.firebaseio.com")
                    .build();

            this.firebaseApp = FirebaseApp.initializeApp(options);
        }

        return firebaseApp;
    }

    public static HashMap<String, String> persistenceUnitOverrideConfigs() {

        final HashMap<String, String> map = new HashMap<>(1);

        map.put(PersistenceUnitProperties.JDBC_URL, System.getProperty("app.db.jdbc.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"));

        return map;
    }

    public ClientConfig clientConfig() {
        return clientConfig;
    }

    public static AppConfig getInstance() {
        return ourInstance;
    }

}
