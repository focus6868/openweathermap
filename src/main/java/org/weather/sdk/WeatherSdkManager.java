package org.weather.sdk;

import org.weather.sdk.exception.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WeatherSdkManager {
    private static WeatherSdkManager instance;
    private final Map<String, WeatherSdk> SdkInstances;

    private WeatherSdkManager() {
        this.SdkInstances = new ConcurrentHashMap<>();
    }

    public static synchronized WeatherSdkManager getInstance() {
        if (instance == null) {
            instance = new WeatherSdkManager();
        }
        return instance;
    }

    public WeatherSdk createSdk(String apiKey, SdkMode mode) throws ApiKeyException {
        if (SdkInstances.containsKey(apiKey)) {
            throw new ApiKeyException("Sdk instance with this API key already exists");
        }

        WeatherSdk Sdk = new WeatherSdk(apiKey, mode);
        SdkInstances.put(apiKey, Sdk);
        return Sdk;
    }

    public WeatherSdk getSdk(String apiKey) {
        return SdkInstances.get(apiKey);
    }

    public void removeSdk(String apiKey) {
        WeatherSdk Sdk = SdkInstances.remove(apiKey);
        if (Sdk != null) {
            Sdk.shutdown();
        }
    }

    public boolean containsSdk(String apiKey) {
        return SdkInstances.containsKey(apiKey);
    }

    public int getInstanceCount() {
        return SdkInstances.size();
    }

    public void shutdownAll() {
        for (WeatherSdk Sdk : SdkInstances.values()) {
            Sdk.shutdown();
        }
        SdkInstances.clear();
    }
}