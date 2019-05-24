package me.nunum.whereami.model.request;

import me.nunum.whereami.model.AlgorithmProvider;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

public class NewAlgorithmProvider {

    @NotNull
    @Size(min = 3, max = 255)
    private String email;

    @Size(min = 3, max = 50)
    private String method;

    private Map<String, String> properties;

    public NewAlgorithmProvider() {
    }

    public NewAlgorithmProvider(String email, String method, Map<String, String> properties) {
        this.email = email;
        this.method = method;
        this.properties = properties;
    }

    public AlgorithmProvider build() {

        AlgorithmProvider.METHOD method = AlgorithmProvider.METHOD.parse(this.method);

        for (String key : method.requiredKeys()) {
            if (!this.properties.containsKey(key)) {
                throw new IllegalArgumentException(String.format("Key %s is required on properties object", key));
            }
        }

        if (method == AlgorithmProvider.METHOD.UNSUPPORTED) {
            throw new IllegalArgumentException("Unsupported provider type");
        }

        return new AlgorithmProvider(email, method, properties);
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "NewAlgorithmProvider{" +
                "email='" + email + '\'' +
                ", method='" + method + '\'' +
                ", properties=" + properties +
                '}';
    }

}
