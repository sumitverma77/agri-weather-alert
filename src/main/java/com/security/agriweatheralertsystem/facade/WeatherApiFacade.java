package com.security.agriweatheralertsystem.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.agriweatheralertsystem.dto.WeatherDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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


    public WeatherDto getWeatherData(String city) {
        String weatherApiUrl = String.format(BASE_URL + FORECAST_URL_TEMPLATE, WEATHER_API_KEY, city);


        try {
            String response = restTemplate.getForObject(weatherApiUrl, String.class);
            JsonNode root = mapper.readTree(response);

            JsonNode forecastDay = root.path("forecast").path("forecastday");

            // Today
            JsonNode today = forecastDay.get(0);
            String todayDate = today.path("date").asText();
            String todayCondition = today.path("day").path("condition").path("text").asText();
            double todayAvgTemp = today.path("day").path("avgtemp_c").asDouble();
            double todayChanceOfRain = today.path("day").path("daily_chance_of_rain").asDouble();
            double todayTotalPrecip = today.path("day").path("totalprecip_mm").asDouble();
            // Tomorrow
            JsonNode tomorrow = forecastDay.get(1);
            String tomorrowDate = tomorrow.path("date").asText();
            String tomorrowCondition = tomorrow.path("day").path("condition").path("text").asText();
            double tomorrowAvgTemp = tomorrow.path("day").path("avgtemp_c").asDouble();
            double tomorrowChanceOfRain = tomorrow.path("day").path("daily_chance_of_rain").asDouble();
            double tomorrowTotalPrecip = tomorrow.path("day").path("totalprecip_mm").asDouble();

            String locationName = root.path("location").path("name").asText();

            return new WeatherDto(locationName, todayDate, todayCondition, todayAvgTemp, todayChanceOfRain,
                    todayTotalPrecip, tomorrowDate, tomorrowCondition, tomorrowAvgTemp, tomorrowChanceOfRain,
                    tomorrowTotalPrecip);

        } catch (Exception e) {
            log.error("Error parsing weather APi response :" + e.getMessage());
            return null;
        }
    }

}
