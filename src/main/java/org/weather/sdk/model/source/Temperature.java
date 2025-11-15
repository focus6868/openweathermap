package org.weather.sdk.model.source;
import org.weather.sdk.model.target.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Temperature {
    public double temp;

    @JsonProperty("feels_like")
    public double feelsLike;


 public Temperature() {}

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(Double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public Temperature(Double temp, Double feelsLike) {
        this.temp = temp;
        this.feelsLike = feelsLike;
    }
}