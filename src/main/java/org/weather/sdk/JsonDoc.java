package org.weather.sdk;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.weather.sdk.exception.ApiKeyException;
import org.weather.sdk.exception.WeatherSdkException;
import org.weather.sdk.mapper.WeatherMapper;
import org.weather.sdk.model.source.WeatherData;
import org.weather.sdk.model.target.WeatherDataInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonDoc {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static WeatherSdk getInstance(String apikey, SdkMode dksmode){
        WeatherSdkManager manager = WeatherSdkManager.getInstance();

        WeatherSdk sdk = null;
        try {
            return manager.createSdk(apikey, dksmode);
        } catch (ApiKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String forecast(WeatherSdk sdk, String city){
        WeatherData weatherSource = null;
        try {
            weatherSource = sdk.getWeatherByCity(city);
        } catch (WeatherSdkException e) {
            throw new RuntimeException(e);
        }
        WeatherDataInfo weatherTarget = new WeatherMapper().mapToTarget(weatherSource);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(weatherTarget);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> forecastAllCities(WeatherSdk sdk){
            return sdk.getWeatherDataInfoAll().stream().map(data -> {
                try {
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
    }
}
