package com.security.agriweatheralertsystem.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.agriweatheralertsystem.dto.WeatherDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class WeatherApiFacade {
    @Value("${weather.api.base.url}")
    private String BASE_URL;

    @Value("${weather.api.key}")
    private String WEATHER_API_KEY;
    @Autowired
    @Qualifier("restTemplate")
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper mapper;
    public static final String FORECAST_URL_TEMPLATE = "v1/forecast.json?key=%s&q=%s&days=2&aqi=no&alerts=no";

    public Optional<WeatherDto> getWeatherData(String city) {
        String weatherApiUrl = String.format(BASE_URL + FORECAST_URL_TEMPLATE, WEATHER_API_KEY, city);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<WeatherDto> response = restTemplate.exchange(
                    weatherApiUrl,
                    HttpMethod.GET,
                    entity,
                    WeatherDto.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return Optional.of(response.getBody());
            } else {
                log.warn("Weather API returned non-OK status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error fetching weather data: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }
}