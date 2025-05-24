package com.security.agriweatheralertsystem.dto;

public class WeatherDTO {

    private String cityName;
    private String conditionText;
    private double temperatureC;

    public WeatherDTO() {
    }

    public WeatherDTO(String cityName, String conditionText, double temperatureC) {
        this.cityName = cityName;
        this.conditionText = conditionText;
        this.temperatureC = temperatureC;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
    }

    public double getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(double temperatureC) {
        this.temperatureC = temperatureC;
    }
}
