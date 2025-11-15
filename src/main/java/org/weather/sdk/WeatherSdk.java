
package org.weather.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.weather.sdk.exception.*;
import org.weather.sdk.mapper.WeatherMapper;
import org.weather.sdk.model.source.WeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.weather.sdk.model.target.WeatherDataInfo;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * This is the main SDK class.
 * It is used to create objects whose primary function is to execute requests to a server with an open API.
 * example of using:
 * <pre>
 * {@code
 * package com.attempt;
 *
 * import org.weather.sdk.*;
 * import com.fasterxml.jackson.databind.ObjectMapper;
 *
 * public class Main {
 *     public static void main(String[] args) {
 *             System.out.println("Hello and welcome!");
 *             ObjectMapper mapper = new ObjectMapper();
 *         String apiKey = "your API key obtained from the weather website";
 *
 *         WeatherSdk sdk = JsonDoc.getInstance(apiKey, SdkMode.ON_DEMAND);
 *         System.out.println(JsonDoc.forecast(sdk, "London"));
 *     }
 * }
 * }
 * </pre>
 * Two modes are available:
 * <ul>
 * <li> ON_DEMAND</li>
 * <li> POLLING</li>
 * </ul>
 * In polling mode, the cached data is updated with data received from the weather website every 10 minutes.
 * */
public class WeatherSdk {
    private final String apiKey;
    private final SdkMode mode;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Map<String, CacheEntry> cache;
    private final ScheduledExecutorService scheduler;

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final int CACHE_SIZE = 10;
    private static final long CACHE_TTL = 10 * 60 * 1000; // 10 минут в миллисекундах
    private static final long POLLING_INTERVAL = 10 * 60 * 1000; // 10 минут для режима опроса

    private static class CacheEntry {
        WeatherData data;
        long timestamp;

        CacheEntry(WeatherData data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL;
        }
    }

    public WeatherSdk(String apiKey, SdkMode mode) throws ApiKeyException {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ApiKeyException("API key cannot be null or empty");
        }

        this.apiKey = apiKey;
        this.mode = mode;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.cache = new ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        if (mode == SdkMode.POLLING) {
            startPolling();
        }
    }

    private void startPolling() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                refreshAllCachedCities();
            } catch (Exception e) {
                System.err.println("Error during polling: " + e.getMessage());
            }
        }, POLLING_INTERVAL, POLLING_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void refreshAllCachedCities() {
        for (String city : cache.keySet()) {
            try {
                WeatherData freshData = fetchWeatherDataFromAPI(city);
                cache.put(city, new CacheEntry(freshData, System.currentTimeMillis()));
            } catch (Exception e) {
                System.err.println("Failed to refresh data for city: " + city);
            }
        }
    }

    public WeatherData getWeatherByCity(String cityName) throws WeatherSdkException {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be null or empty");
        }

        String normalizedCityName = cityName.trim().toLowerCase();

        // Проверяем кэш
        CacheEntry cached = cache.get(normalizedCityName);
        if (cached != null && !cached.isExpired()) {
            return cached.data;
        }

        // Если данных нет в кэше или они устарели, запрашиваем из API
        WeatherData weatherData = fetchWeatherDataFromAPI(cityName);

        // Обновляем кэш
        updateCache(normalizedCityName, weatherData);

        return weatherData;
    }

    public List<WeatherDataInfo> getWeatherDataInfoAll(){
        WeatherMapper mapper = new WeatherMapper();
        return cache.values().stream().map(cacheEntry -> mapper.mapToTarget(cacheEntry.data)).toList();
    }

    private WeatherData fetchWeatherDataFromAPI(String cityName) throws WeatherSdkException {
        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric",
                    BASE_URL, cityName, apiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .timeout(java.time.Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            return handleResponse(response, cityName);

        } catch (WeatherSdkException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherSdkException("Failed to fetch weather data: " + e.getMessage(), e.getCause());
        }
    }

    private WeatherData handleResponse(HttpResponse<String> response, String cityName)
            throws WeatherSdkException {
        int statusCode = response.statusCode();
        String body = response.body();

        switch (statusCode) {
            case 200:
                try {
                    return objectMapper.readValue(body, WeatherData.class);
                } catch (Exception e) {
                    throw new WeatherSdkException("Failed to parse weather data: " + e.getMessage(), e.getCause());
                }

            case 401:
                throw new ApiKeyException("Invalid API key " + statusCode);

            case 404:
                throw new CityNotFoundException(cityName);

            case 429:
                throw new WeatherSdkException("API rate limit exceeded " + statusCode);

            case 500:
            case 502:
            case 503:
                throw new WeatherSdkException("Weather service unavailable " + statusCode);

            default:
                throw new WeatherSdkException("Unexpected error: HTTP " + statusCode);
        }
    }

    private void updateCache(String cityName, WeatherData data) {
        if (cache.size() >= CACHE_SIZE) {
            // Удаляем самый старый элемент - с наименьшим значением entry.getValue().timestamp
            String oldestCity = null;
            long oldestTimestamp = Long.MAX_VALUE;

            for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
                if (entry.getValue().timestamp < oldestTimestamp) {
                    oldestTimestamp = entry.getValue().timestamp;
                    oldestCity = entry.getKey();
                }
            }

            if (oldestCity != null) {
                cache.remove(oldestCity);
            }
        }
        cache.put(cityName, new CacheEntry(data, System.currentTimeMillis()));
    }

    public void clearCache() {
        cache.clear();
    }

    public int getCacheSize() {
        return cache.size();
    }

    public SdkMode getMode() {
        return mode;
    }

    public ObjectMapper getObjectMapper(){return objectMapper;}

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}