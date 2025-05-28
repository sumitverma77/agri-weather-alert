package com.security.agriweatheralertsystem.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.agriweatheralertsystem.enums.FallbackMessage;
import com.security.agriweatheralertsystem.enums.Language;
import com.security.agriweatheralertsystem.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class GeminiAIServiceImpl implements AIService {

    @Value("${gemini.api.key}")
    private String GEMINI_API_KEY;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Optional<String> getGeminiResponse( String prompt)
    {
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
            String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(geminiRequestBody, headers);
            String geminiResponse = restTemplate.postForObject(geminiApiUrl, entity, String.class);

            JsonNode root = mapper.readTree(geminiResponse);
            return Optional.ofNullable(root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText());
        } catch (Exception e) {
            System.err.println("Error parsing Gemini API response: " + e.getMessage());
           return Optional.empty();
        }
    }
}
