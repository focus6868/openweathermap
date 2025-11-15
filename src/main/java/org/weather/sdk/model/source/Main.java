package org.weather.sdk.model.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Main {
    public double temp;
    @JsonProperty("feels_like")
    public double feelsLike;
    @JsonProperty("temp_min")
    public double tempMin;
    @JsonProperty("temp_max")
    public double tempMax;
    public int pressure;
    public int humidity;
    @JsonProperty("sea_level")
    public int seaLevel;
    @JsonProperty("grnd_level")
    public int grndLevel;
}