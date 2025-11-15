package org.weather.sdk.mapper;

import org.weather.sdk.model.source.*;
import org.weather.sdk.model.target.WeatherDataInfo;

public class WeatherMapper {
    public WeatherMapper(){}

    public WeatherDataInfo mapToTarget(WeatherData sourceWeather){
        WeatherDataInfo targetWeather = new WeatherDataInfo();

        targetWeather.setWeather(
                new Weather()
        );
        targetWeather.getWeather().setMain(sourceWeather.getWeather().get(0).getMain());
        targetWeather.getWeather().setDescription(sourceWeather.getWeather().get(0).getDescription());

        targetWeather.setTemperature(
                new Temperature()
        );
        targetWeather.getTemperature().setTemp(sourceWeather.getMain().getTemp());
        targetWeather.getTemperature().setFeelsLike(sourceWeather.getMain().getFeelsLike());

        targetWeather.setSys(
                new Sys()
        );
        targetWeather.getSys().setSunrise(sourceWeather.getSys().getSunrise());
        targetWeather.getSys().setSunset(sourceWeather.getSys().getSunset());

        targetWeather.setWind(
                new Wind()
        );
        targetWeather.getWind().setSpeed(sourceWeather.getWind().getSpeed());

        targetWeather.setDt(sourceWeather.getDt());
        targetWeather.setTimezone(sourceWeather.getTimezone());
        targetWeather.setName(sourceWeather.getName());
        targetWeather.setVisibility(sourceWeather.getVisibility());

        return targetWeather;
    }
}
