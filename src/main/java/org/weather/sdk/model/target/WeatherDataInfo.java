package org.weather.sdk.model.target;

import org.weather.sdk.model.source.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherDataInfo {

    public Weather weather;
    public Temperature temperature;
    public int visibility;
    public Wind wind;
    public long datetime;
    public Sys sys;
    public int timezone;
    public String name;

    public WeatherDataInfo() {}

    public Weather getWeather() {
        return weather;
    }
    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Integer getTimezone() {
        return timezone;
    }
    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public Sys getSys() {
        return sys;
    }
    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setTemperature(Temperature temperature){
        this.temperature=temperature;
    }
    public Temperature getTemperature(){return this.temperature;}

    public void setVisibility(int visibility){this.visibility=visibility;}
    public int getVisibility(){return this.visibility;}

    public void setWind(Wind wind) {this.wind = wind;}
    public Wind getWind() {return wind;}

    public void setDt(long datetime) {this.datetime=datetime;}
    public Long getDt() {return datetime;}

}