package com.security.agriweatheralertsystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.agriweatheralertsystem.constant.Constants;
import com.security.agriweatheralertsystem.converter.Converter;
import com.security.agriweatheralertsystem.dto.ParsedMessage;
import com.security.agriweatheralertsystem.dto.WeatherDto;
import com.security.agriweatheralertsystem.entity.User;
import com.security.agriweatheralertsystem.enums.FallbackMessage;
import com.security.agriweatheralertsystem.enums.IntentType;
import com.security.agriweatheralertsystem.enums.Language;
import com.security.agriweatheralertsystem.facade.WeatherApiFacade;
import com.security.agriweatheralertsystem.repository.UserRepo;
import com.security.agriweatheralertsystem.utils.JsonUtil;
import com.security.agriweatheralertsystem.utils.PromptBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class WeatherService {
    @Autowired
    private AIService aiService;
    @Autowired
    MessagingService messagingService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    @Qualifier("restTemplate")
    RestTemplate restTemplate;

    @Autowired
    WeatherApiFacade weatherApiFacade;
    @Autowired
    ObjectMapper mapper;

    public Optional<String> handleWeatherRequest(String phoneNumber, String messageBody) {
        ParsedMessage parsedMessage = parseMessage(messageBody);

        String intent = parsedMessage.getIntent();
        String location = parsedMessage.getLocation();
        String language = parsedMessage.getLanguage();

        log.info("{}: {}, {}: {}, {}: {}", Constants.INTENT, intent, Constants.LANGUAGE, language, Constants.LOCATION, location);

        if (intent.equals(IntentType.UPDATE_LOCATION.getValue()) && !location.isEmpty()) {
            updateUserPreferences(phoneNumber, location, Language.fromString(language));
            return Optional.of(FallbackMessage.LOCATION_UPDATE_SUCCESS
                    .getMessage(Language.fromString(language)).formatted(location));

        } else if (intent.equals(IntentType.GET_WEATHER.getValue()) && !location.isEmpty()) {
            Optional<WeatherDto> weatherData = weatherApiFacade.getWeatherData(location);
            return weatherData
                    .map(dto -> summarize(dto, location, Language.fromString(language)))
                    .orElse(Optional.ofNullable(FallbackMessage.WEATHER_INFO_UNAVAILABLE.getMessage(Language.fromString(language))));

        } else {
            return Optional.of(FallbackMessage.UNKNOWN_REQUEST
                    .getMessage(Language.fromString(language)));
        }
    }

    public void sendWeatherAlert(String phoneNumber, String messageBody) {
        Optional<String> message = handleWeatherRequest(phoneNumber, messageBody);
        message.ifPresent(msg -> messagingService.sendMessage(phoneNumber, msg));
    }


    public Optional<String> summarize(WeatherDto weatherData, String location, Language language) {
        String prompt = PromptBuilder.WeatherSummaryPrompt(weatherData, location, language);
        return aiService.getResponse(prompt);
    }

    public void updateUserPreferences(String phoneNumber, String location, Language language) {
        String cleanPhone = phoneNumber.startsWith("whatsapp:") ? phoneNumber.substring("whatsapp:".length())
                : phoneNumber;
        User user = userRepo.findByPhone(cleanPhone).orElseGet(() -> Converter.toUserEntity(cleanPhone, location, language));
        user.setLocation(location);
        user.setLanguage(language);
        userRepo.save(user);
    }

    public ParsedMessage parseMessage(String messageBody) {
        String prompt = PromptBuilder.IntentAndLocationPrompt(messageBody);
        Map<String, String> details = extractDetailsWithAI(prompt);

        String intent = details.get(Constants.INTENT);
        String location = details.get(Constants.LOCATION);
        String language = details.get(Constants.LANGUAGE);

        return new ParsedMessage(intent, location, language);
    }

    public Map<String, String> extractDetailsWithAI(String prompt) {
        return aiService.getResponse(prompt)
                .map(JsonUtil::cleanJson)
                .map(this::parseJsonToMap)
                .orElseGet(() -> Map.of(Constants.INTENT, Constants.NONE, Constants.LOCATION, Constants.EMPTY, Constants.LANGUAGE, Constants.NONE));
    }

    private Map<String, String> parseJsonToMap(String json) {
        try {
            Map<String, String> parsed = mapper.readValue(json, new TypeReference<>() {
            });
            return Map.of(
                    Constants.INTENT, parsed.getOrDefault(Constants.INTENT, Constants.NONE),
                    Constants.LOCATION, parsed.getOrDefault(Constants.LOCATION, Constants.EMPTY),
                    Constants.LANGUAGE, parsed.getOrDefault(Constants.LANGUAGE, Constants.NONE)
            );
        } catch (Exception e) {
            log.error("Failed to parse cleaned JSON: {}", e.getMessage());
            return Map.of(Constants.INTENT, Constants.NONE, Constants.LOCATION, Constants.EMPTY, Constants.LANGUAGE, Constants.NONE);
        }
    }

}
