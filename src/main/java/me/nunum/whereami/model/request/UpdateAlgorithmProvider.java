package me.nunum.whereami.model.request;

import me.nunum.whereami.model.AlgorithmProvider;

import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

public class UpdateAlgorithmProvider {


    @Size(min = 3, max = 50)
    private String method;

    private Map<String, String> properties;

    public UpdateAlgorithmProvider() {
        this("", new HashMap<>());
    }

    public UpdateAlgorithmProvider(String method, Map<String, String> properties) {
        this.method = method;
        this.properties = properties;
    }

    public AlgorithmProvider updateProvider(AlgorithmProvider provider) {

        AlgorithmProvider.METHOD method = null;

        if (this.method != null) {
            method = AlgorithmProvider.METHOD.parse(this.method);

            if (method == AlgorithmProvider.METHOD.UNSUPPORTED) {
                throw new IllegalArgumentException("Unsupported provider type");
            }

            if (!provider.getMethod().equals(method)) {
                for (String key : method.requiredKeys()) {
                    if (!this.properties.containsKey(key)) {
                        throw new IllegalArgumentException(String.format("Key %s is required on properties object", key));
                    }
                }
                provider.getProperties().clear();
            }
        }

        this.properties.forEach((k, v) -> {
            provider.getProperties().put(k, v);
        });

        return provider;
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
        return "UpdateAlgorithmProvider{" +
                "method='" + method + '\'' +
                ", properties=" + properties +
                '}';
    }
}
