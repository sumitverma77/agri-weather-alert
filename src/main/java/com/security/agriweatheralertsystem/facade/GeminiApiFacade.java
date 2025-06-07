package com.security.agriweatheralertsystem.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class GeminiApiFacade {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.base.url}")
    private String baseUrl;

    private static final String ENDPOINT = "v1beta/models/gemini-2.0-flash:generateContent?key=";

    public Optional<String> callGeminiApi(String requestBody) {
        try {
            String url = baseUrl + ENDPOINT + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.postForObject(url, entity, String.class);

            return Optional.of(response);
        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            return Optional.empty();
        }
    }
}
