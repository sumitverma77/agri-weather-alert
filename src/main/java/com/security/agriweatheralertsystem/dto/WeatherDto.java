package com.security.agriweatheralertsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WeatherDto {

    private Location location;
    private Forecast forecast;

    @Data
    public static class Location {
        private String name;
    }

    @Data
    public static class Forecast {
        private List<ForecastDay> forecastday;
    }

    @Data
    public static class ForecastDay {
        private String date;
        private Day day;
    }

    @Data
    public static class Day {
        private double avgtempC;
        private double dailyChanceOfRain;
        private double totalprecipMm;
        private Condition condition;
    }

    @Data
    public static class Condition {
        private String text;
    }
}

