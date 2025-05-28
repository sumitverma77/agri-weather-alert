package com.security.agriweatheralertsystem.utils;

import com.security.agriweatheralertsystem.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class LocationParser {

    @Autowired
    private AIService geminiService;
    public Optional<Map<String, String>> parseLocation(String messageBody) {
        try {
            String prompt = """
Find the most suitable single-word location from these options for weather forecasting: %s. Return only the single word location without any explanation.
I'm using weatherAPI so return that location from that which is available in weatherAPI.
""".formatted(messageBody);

            Optional<String> nearestLocationOpt = geminiService.getGeminiResponse(prompt);
            if (nearestLocationOpt.isPresent()) {
                String nearestLocation = nearestLocationOpt.get();
                Map<String, String> locationMap = new HashMap<>();
                locationMap.put("originalLocation", messageBody);
                locationMap.put("parsedLocation", nearestLocation);
                return Optional.of(locationMap);
            } else {
                return Optional.empty();
            }

        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            return Optional.empty();
        }
    }

}
