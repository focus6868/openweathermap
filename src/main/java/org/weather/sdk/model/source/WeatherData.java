package org.weather.sdk.model.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
/**
 * This class is used to convert a response in JSON format into a Java object.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    public List<Weather> weather;
    public Temperature main;
    public int visibility;
    public Wind wind;

    @JsonProperty("dt")
    public long dt;

    public Sys sys;
    public int timezone;
    public String name;

    // Конструкторы, геттеры и сеттеры
    public WeatherData() {}

    // Геттеры и сеттеры
    public List<Weather> getWeather() { return weather; }
    public void setWeather(List<Weather> weather) { this.weather = weather; }

    public Temperature getMain() { return main; }
    public void setMain(Temperature main) { this.main = main; }

    public Integer getVisibility() { return visibility; }
    public void setVisibility(Integer visibility) { this.visibility = visibility; }

    public Wind getWind() { return wind; }
    public void setWind(Wind wind) { this.wind = wind; }

    public Long getDt() { return dt; }
    public void setDt(Long dt) { this.dt = dt; }

    public Sys getSys() { return sys; }
    public void setSys(Sys sys) { this.sys = sys; }

    public Integer getTimezone() { return timezone; }
    public void setTimezone(int timezone) { this.timezone = timezone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        getWeather().forEach(w -> sb.append(w).append(";"));

        return sb.toString();
    }
}