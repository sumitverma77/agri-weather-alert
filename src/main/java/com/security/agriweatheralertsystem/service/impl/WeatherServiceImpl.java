package com.security.agriweatheralertsystem.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.agriweatheralertsystem.dto.WeatherDTO;
import com.security.agriweatheralertsystem.service.LocationService;
import com.security.agriweatheralertsystem.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Value("${weatherapi.key}")
    private String WEATHERAPI_KEY;

    @Value("${gemini.api.key}")
    private String GEMINI_API_KEY;

    @Autowired
    private LocationService locationService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public WeatherDTO getWeatherData(String messageBody) {
        String city = locationService.parseLocation(messageBody);
        String weatherApiUrl = String.format(
                "https://api.weatherapi.com/v1/forecast.json?key=%s&q=%s&days=2&aqi=no&alerts=no",
                WEATHERAPI_KEY, city);

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

            return new WeatherDTO(locationName, todayDate, todayCondition, todayAvgTemp, todayChanceOfRain, todayTotalPrecip,
                    tomorrowDate, tomorrowCondition, tomorrowAvgTemp, tomorrowChanceOfRain, tomorrowTotalPrecip);

        } catch (Exception e) {
            System.err.println("Error parsing weather API response: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Map<String, String> summarize(WeatherDTO weatherData, String location) {
        String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build prompt with explicit fields including rain chance and precipitation
        String prompt = buildGeminiPrompt(weatherData, location);

        // Escape double quotes for JSON string embedding
        String escapedPrompt = prompt.replace("\"", "\\\"");

        String geminiRequestBody = """
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "%s"
                }
              ]
            }
          ]
        }
        """.formatted(escapedPrompt);

        try {
            HttpEntity<String> entity = new HttpEntity<>(geminiRequestBody, headers);
            String geminiResponse = restTemplate.postForObject(geminiApiUrl, entity, String.class);

            JsonNode root = mapper.readTree(geminiResponse);
            String fullSummary = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            // Remove possible duplicated prefixes like "English: "
            String cleaned = fullSummary.replaceFirst("(?i)^English:\\s*", "").trim();

            // Split at "Hindi:"
            String[] parts = cleaned.split("(?i)Hindi:\\s*", 2);

            Map<String, String> summaries = new HashMap<>();
            summaries.put("english", parts[0].trim());
            summaries.put("hindi", parts.length > 1 ? parts[1].trim() : "Hindi summary not available");

            return summaries;
        } catch (Exception e) {
            System.err.println("Error parsing Gemini API response: " + e.getMessage());
            Map<String, String> fallback = new HashMap<>();
            fallback.put("english", "Could not retrieve weather information.");
            fallback.put("hindi", "मौसम की जानकारी प्राप्त नहीं हो सकी।");
            return fallback;
        }
    }

    private String buildGeminiPrompt(WeatherDTO data, String location) {
        return """
            Generate a concise, farmer-friendly weather summary (3-4 lines) including today's and tomorrow's forecast with temperature, rain probability, and rainfall amount.
            Provide the summary in English and Hindi with a practical farming tip related to weather.
            Format:
            English: <summary>
            Hindi: <summary>
            """.formatted(
                location,
                data.getTodayDate(), data.getTodayCondition(), data.getTodayAvgTemp(), data.getTodayChanceOfRain(), data.getTodayTotalPrecip(),
                data.getTomorrowDate(), data.getTomorrowCondition(), data.getTomorrowAvgTemp(), data.getTomorrowChanceOfRain(), data.getTomorrowTotalPrecip()
        );
    }
}
