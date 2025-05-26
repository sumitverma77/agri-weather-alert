package com.security.agriweatheralertsystem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.agriweatheralertsystem.dto.WeatherDTO;
import com.security.agriweatheralertsystem.enums.FallbackMessage;
import com.security.agriweatheralertsystem.enums.Language;
import com.security.agriweatheralertsystem.utils.LocationParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WeatherService {
    @Value("${weatherapi.key}")
    private String WEATHERAPI_KEY;

    @Value("${gemini.api.key}")
    private String GEMINI_API_KEY;

    @Autowired
    private AIService geminiService;

    @Autowired
    private LocationParser locationParser;
    @Autowired
    MessagingService messagingService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    public void sendWeatherAlert(String phoneNumber, String messageBody) {
        System.out.println("Received message from: " + phoneNumber);
        System.out.println("Message body: " + messageBody);

        WeatherDTO weatherData = getWeatherData(messageBody);
        if (weatherData != null) {
            String hindiSummary = summarize(weatherData, messageBody, Language.HINDI);
            String englishSummary = summarize(weatherData, messageBody, Language.ENGLISH);

            if (hindiSummary != null && englishSummary != null) {

                messagingService.sendMessage(phoneNumber, englishSummary);
                messagingService.sendMessage(phoneNumber, hindiSummary);
            } else {
                messagingService.sendMessage(phoneNumber, FallbackMessage.getMessage(Language.ENGLISH));
            }
        } else {
            messagingService.sendMessage(phoneNumber, FallbackMessage.getMessage(Language.ENGLISH));
        }
    }
    public WeatherDTO getWeatherData(String messageBody) {
        Map<String, String> locationMap = locationParser.parseLocation(messageBody);
        String city = locationMap != null ? locationMap.get("parsedLocation") : null;
        String originalLocation = locationMap != null ? locationMap.get("originalLocation") : null;

        System.out.println("Original Location: " + originalLocation);
        System.out.println("Parsed Location: " + city);

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


    public String summarize(WeatherDTO weatherData, String location, Language language) {
        String prompt = buildGeminiPrompt(weatherData, location , language);
        return geminiService.getGeminiResponse(prompt);
    }

    private String buildGeminiPrompt(WeatherDTO data, String location , Language language) {
        return """
Generate a concise, farmer-friendly weather summary (3-4 lines) for %s.
Include:
- Today's (%s) forecast: %s, Avg Temp: %s°C, Rain Chance: %s%%, Rainfall: %smm
- Tomorrow's (%s) forecast: %s, Avg Temp: %s°C, Rain Chance: %s%%, Rainfall: %smm

Give the response in %s language

Also, include one tip for our farmers relevant to the weather.
Do NOT include phrases like "Here is your response" or any system messages.
Directly write the report as if it's ready to be sent to a user via WhatsApp or SMS.
""".formatted(
                location,
                data.getTodayDate(), data.getTodayCondition(), data.getTodayAvgTemp(), data.getTodayChanceOfRain(), data.getTodayTotalPrecip(),
                data.getTomorrowDate(), data.getTomorrowCondition(), data.getTomorrowAvgTemp(), data.getTomorrowChanceOfRain(), data.getTomorrowTotalPrecip()
                , language
        );

    }
}
