package com.security.agriweatheralertsystem.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.agriweatheralertsystem.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

@Component
public class LocationParser {

    @Autowired
    private AIService geminiService;


    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, String> parseLocation(String messageBody) {
        try {
            String prompt = """
Find the most suitable single-word location from these options for weather forecasting: %s. Return only the single word location without any explanation.
I'm using weatherAPI so return that location from that which is available in weatherAPI.
""".formatted(messageBody);
            String nearestLocation =  geminiService.getGeminiResponse(prompt);
            Map<String, String> locationMap = new HashMap<>();
            locationMap.put("originalLocation", messageBody);
            locationMap.put("parsedLocation", nearestLocation);
            return locationMap;
        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            return null;
        }
    }

}
