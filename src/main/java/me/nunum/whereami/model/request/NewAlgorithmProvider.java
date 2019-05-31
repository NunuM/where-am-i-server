package me.nunum.whereami.model.request;

import me.nunum.whereami.model.AlgorithmProvider;
import me.nunum.whereami.model.Provider;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

public class NewAlgorithmProvider {


    @NotNull
    @Size(min = 3, max = 50)
    private String method;

    @NotNull
    private Map<String, String> properties;

    public NewAlgorithmProvider() {
        this("", new HashMap<>());
    }

    public NewAlgorithmProvider(String method, Map<String, String> properties) {
        this.method = method;
        this.properties = properties;
    }

    public AlgorithmProvider build(Provider provider) {

        AlgorithmProvider.METHOD selectedMethod = AlgorithmProvider.METHOD.parse(this.method);

        if (this.properties == null) {
            throw new IllegalArgumentException(String.format("Expecting properties key to be set"));
        }

        for (String key : selectedMethod.requiredKeys()) {
            if (!this.properties.containsKey(key)) {
                throw new IllegalArgumentException(String.format("Key %s is required on properties object", key));
            }
        }

        if (selectedMethod == AlgorithmProvider.METHOD.UNSUPPORTED) {
            throw new IllegalArgumentException("Unsupported provider type");
        }

        return new AlgorithmProvider(provider, selectedMethod, properties);
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
                ", method='" + method + '\'' +
                ", properties=" + properties +
                '}';
    }

}
