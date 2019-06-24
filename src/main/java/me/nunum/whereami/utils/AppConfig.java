package me.nunum.whereami.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import me.nunum.whereami.framework.interceptor.ClientLoggingInterceptor;
import org.glassfish.jersey.client.ClientConfig;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AppConfig {

    private static final AppConfig ourInstance = new AppConfig();

    private FirebaseApp firebaseApp;
    private final ClientConfig clientConfig;

    private AppConfig() {
        clientConfig = new ClientConfig();
        clientConfig.register(ClientLoggingInterceptor.class);
    }

    public static final String JPA_UNIT = System.getProperty("app.persistence.unit", "me.nunum.whereami.JPA_PERSISTENCE");


    public static final String X_APP_HEADER = "X-APP";


    public static final String EMAIL_HOST = "smtp.nunum.me";
    public static final String EMAIL_FROM = "no-reply@whereami.nunum.me";


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

    public ClientConfig clientConfig() {
        return clientConfig;
    }

    public static AppConfig getInstance() {
        return ourInstance;
    }

}
