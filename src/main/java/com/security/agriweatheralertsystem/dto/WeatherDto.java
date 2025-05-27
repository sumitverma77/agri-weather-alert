package com.security.agriweatheralertsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class WeatherDto {
    private String locationName;
    private String todayDate;
    private String todayCondition;
    private double todayAvgTemp;
    private double todayChanceOfRain;
    private double todayTotalPrecip;
    private String tomorrowDate;
    private String tomorrowCondition;
    private double tomorrowAvgTemp;
    private double tomorrowChanceOfRain;
    private double tomorrowTotalPrecip;
}

