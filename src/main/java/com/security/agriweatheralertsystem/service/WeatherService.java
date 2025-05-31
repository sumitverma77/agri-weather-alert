package com.security.agriweatheralertsystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.agriweatheralertsystem.converter.Converter;
import com.security.agriweatheralertsystem.dto.WeatherDto;
import com.security.agriweatheralertsystem.enums.FallbackMessage;
import com.security.agriweatheralertsystem.enums.Language;
import com.security.agriweatheralertsystem.repository.UserRepo;
import com.security.agriweatheralertsystem.utils.PromptBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class WeatherService {
    @Value("${weatherapi.key}")
    private String WEATHERAPI_KEY;

    @Value("${gemini.api.key}")
    private String GEMINI_API_KEY;

    @Autowired
    private AIService aiService;

    @Autowired
    MessagingService messagingService;
    @Autowired
    UserRepo userRepo;


    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public void sendWeatherAlert(String phoneNumber, String messageBody) {
        System.out.println("Received message from: " + phoneNumber);
        System.out.println("Message body: " + messageBody);


        String prompt = PromptBuilder.IntentAndLocationPrompt(messageBody);
        Map<String, String> intentAndLocation = extractIntentAndLocation(prompt);
        String intent = intentAndLocation.get("intent");
        String location = intentAndLocation.get("location");
        String language = intentAndLocation.get("language");
        System.out.println("Intent: " + intent + "Location: " + location + "Language: " + language);

        if (intent.equals("update_location") && !location.isEmpty()) {
            updateUserPreferences(phoneNumber, location , Language.fromString(language));

            messagingService.sendMessage(phoneNumber, FallbackMessage.LOCATION_UPDATE_SUCCESS.getMessage(Language.fromString(language)).formatted(location));
        } else if (intent.equals("get_weather") && !location.isEmpty()) {
            WeatherDto   weatherData = getWeatherData(location);
            Optional<String> summaryOpt = summarize(weatherData, location, Language.fromString(language));

            if (summaryOpt.isPresent()) {
                messagingService.sendMessage(phoneNumber, summaryOpt.get());
            } else {
                messagingService.sendMessage(phoneNumber, FallbackMessage.WEATHER_FETCH_FAILED.getMessage(Language.fromString(language)).formatted(location));
            }
        }
        else if(intent.equals("none") )
        {
            messagingService.sendMessage(phoneNumber, FallbackMessage.UNKNOWN_REQUEST.getMessage(Language.fromString(language)));
        }
        else {

            messagingService.sendMessage(phoneNumber, FallbackMessage.UNKNOWN_REQUEST.getMessage(Language.fromString(language)));
        }

    }

    public WeatherDto getWeatherData(String city) {

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

            return new WeatherDto(locationName, todayDate, todayCondition, todayAvgTemp, todayChanceOfRain, todayTotalPrecip,
                    tomorrowDate, tomorrowCondition, tomorrowAvgTemp, tomorrowChanceOfRain, tomorrowTotalPrecip);

        } catch (Exception e) {
            System.err.println("Error parsing weather API response: " + e.getMessage());
            return null;
        }
    }


    public Optional<String> summarize(WeatherDto weatherData, String location, Language language) {
        String prompt = PromptBuilder.WeatherSummaryPrompt(weatherData, location, language);
        return aiService.getResponse(prompt);
    }
    private void updateUserPreferences (String phoneNumber, String location, Language language) {

        userRepo.findByPhone(phoneNumber)
                .ifPresentOrElse(
                        user -> {
                            user.setLocation(location);
                            user.setLanguage(language);
                            userRepo.save(user);
                        },
                        () -> {
                            Converter.toUserEntity(phoneNumber, location, language)
                                    .ifPresent(userRepo::save);
                        }
                );
    }


    private Map<String, String> extractIntentAndLocation(String prompt) {
        try {
            Optional<String> response = aiService.getResponse(prompt);

            if (response.isPresent()) {
                String json = response.get();

                // Clean up json string: remove backticks and leading/trailing labels like "Optional[```json ... ```]"
                json = json.trim();

                // Remove Optional[...] wrapper if present
                if (json.startsWith("Optional[")) {
                    json = json.substring("Optional[".length(), json.length() - 1).trim();
                }

                // Remove backticks ``` and optional "json" label (case-insensitive)
                json = json.replaceAll("(?i)^```json", "");  // remove leading ```json (ignore case)
                json = json.replaceAll("^```", "");          // remove leading ```
                json = json.replaceAll("```$", "");          // remove trailing ```
                json = json.trim();

                ObjectMapper mapper = new ObjectMapper();

                // Parse the cleaned JSON string to Map
                Map<String, String> parsed = mapper.readValue(json, new TypeReference<>() {
                });

                String intent = parsed.getOrDefault("intent", "none");
                String location = parsed.getOrDefault("location", "");
                String language = parsed.getOrDefault("language", "unknown");

                return Map.of(
                        "intent", intent,
                        "location", location,
                        "language", language
                );
            }
        } catch (Exception e) {
            System.err.println("Error extracting intent/location/language: " + e.getMessage());
        }

        return Map.of(
                "intent", "none",
                "location", "",
                "language", "unknown"
        );
    }

}
