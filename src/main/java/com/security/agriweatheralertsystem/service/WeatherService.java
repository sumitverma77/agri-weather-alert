package com.security.agriweatheralertsystem.service;

import com.security.agriweatheralertsystem.dto.WeatherDTO;

import java.util.Map;

public interface WeatherService {

    WeatherDTO getWeatherData(String messageBody);

    Map<String, String> summarize(WeatherDTO weatherData, String location);
}
