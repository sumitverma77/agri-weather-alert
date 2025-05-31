package com.security.agriweatheralertsystem.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.agriweatheralertsystem.facade.GeminiApiFacade;
import com.security.agriweatheralertsystem.service.AIService;
import com.security.agriweatheralertsystem.utils.RequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeminiAIServiceImpl implements AIService {

    @Autowired
    private GeminiApiFacade geminiApiFacade;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public Optional<String> getResponse(String prompt) {
        String requestBody = RequestBuilder.geminiRequestBuilder(prompt);

        return geminiApiFacade.callGeminiApi(requestBody)
                .flatMap(response -> {
                    try {
                        JsonNode root = mapper.readTree(response);
                        return Optional.ofNullable(
                                root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText()
                        );
                    } catch (Exception e) {
                        System.err.println("Error parsing Gemini response: " + e.getMessage());
                        return Optional.empty();
                    }
                });
    }
}
