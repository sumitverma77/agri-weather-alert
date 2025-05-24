package com.security.agriweatheralertsystem.service.impl;

import com.security.agriweatheralertsystem.dto.WeatherDTO;
import com.security.agriweatheralertsystem.service.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Value("${weatherapi.key}")
    private String WEATHERAPI_KEY;

    @Override
    public WeatherDTO getWeatherData(String city) {
        String weatherApiUrl = "https://api.weatherapi.com/v1/current.json?key=" + WEATHERAPI_KEY + "&q=" + city;
        RestTemplate restTemplate = new RestTemplate();
        String weatherApiResponse = restTemplate.getForObject(weatherApiUrl, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(weatherApiResponse);
            JsonNode location = root.path("location");
            String cityName = location.path("name").asText();
            JsonNode current = root.path("current");
            String conditionText = current.path("condition").path("text").asText();
            double temperatureC = current.path("temp_c").asDouble();

            return new WeatherDTO(cityName, conditionText, temperatureC);

        } catch (Exception e) {
            System.err.println("Error parsing weather API response: " + e.getMessage());
            return null; // TODO: Handle exception properly
        }
    }
}
