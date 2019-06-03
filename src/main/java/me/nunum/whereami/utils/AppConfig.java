package me.nunum.whereami.utils;

public class AppConfig {

    private AppConfig() {
    }

    public static final String JPA_UNIT = System.getProperty("app.persistence.unit", "me.nunum.whereami.JPA_PERSISTENCE");


    public static final String X_APP_HEADER = "X-APP";


    public static final String EMAIL_HOST = "smtp.nunum.me";
    public static final String EMAIL_FROM = "no-reply@whereami.nunum.me";

}
