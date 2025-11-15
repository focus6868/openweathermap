package org.weather.sdk.exception;

public class CityNotFoundException extends WeatherSdkException {
    public CityNotFoundException(String cityName) {
        super("City not found: " + cityName);
    }
}