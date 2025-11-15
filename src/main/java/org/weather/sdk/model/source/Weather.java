package org.weather.sdk.model.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {
    public String main;
    public String description;

    public Weather(){}

    public void setMain(String main){this.main=main;}
    public String getMain() {
        return main;
    }

    public void setDescription(String description){this.description=description;}
    public String getDescription() {
        return description;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
            sb.append(main).append(", ").append(description);
        return sb.toString();
    }
}