package me.nunum.whereami.model.request;

import me.nunum.whereami.model.AlgorithmProvider;

import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

import static me.nunum.whereami.model.request.NewAlgorithmProvider.isValidURL;

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

        AlgorithmProvider.METHOD selectedMethod = null;

        if (!(this.method == null || this.method.isEmpty())) {

            selectedMethod = AlgorithmProvider.METHOD.parse(this.method);

            if (selectedMethod == AlgorithmProvider.METHOD.UNSUPPORTED) {
                throw new IllegalArgumentException("Unsupported provider type");
            }

            if (!provider.getMethod().equals(selectedMethod)) {
                for (String key : selectedMethod.requiredKeys()) {
                    if (!(this.properties.containsKey(key) && isValidURL(this.properties.get(key)))) {
                        throw new IllegalArgumentException(String.format("Key %s is required on properties object", key));
                    }
                }
                provider.getProperties().clear();
            }
        }

        this.properties.forEach((k, v) ->
                {
                    if (isValidURL(this.properties.get(k))) {
                        provider.getProperties().put(k, v);
                    } else {
                        throw new IllegalArgumentException(String.format("Key %s is not a valid URL", k));
                    }
                }
        );

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
