package com.security.agriweatheralertsystem.service;

import com.security.agriweatheralertsystem.dto.WeatherDTO;

public interface WeatherService {

    WeatherDTO getWeatherData(String city);
}
