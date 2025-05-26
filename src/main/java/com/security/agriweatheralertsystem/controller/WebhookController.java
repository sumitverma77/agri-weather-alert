package com.security.agriweatheralertsystem.controller;

import com.security.agriweatheralertsystem.dto.WeatherDTO;
import com.security.agriweatheralertsystem.service.TwilioService;
import com.security.agriweatheralertsystem.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class WebhookController {

    private final TwilioService twilioService;
    private final WeatherService weatherService;

    @Autowired
    public WebhookController(TwilioService twilioService, WeatherService weatherService) {
        this.twilioService = twilioService;
        this.weatherService = weatherService;
    }

    @PostMapping("/webhook")
    public void receiveMessage(
            @RequestParam("From") String phoneNumber,
            @RequestParam("Body") String messageBody) {

        // Log the message
        System.out.println("Received message from: " + phoneNumber);
        System.out.println("Message body: " + messageBody);

        WeatherDTO weatherData = weatherService.getWeatherData(messageBody);

        if (weatherData != null) {
            Map<String, String> summaries = weatherService.summarize(weatherData, messageBody);

            if (summaries != null) {
                String englishSummary = summaries.get("english");
                String hindiSummary = summaries.get("hindi");

                String message = "English: " + englishSummary + "\\nHindi: " + hindiSummary;
                twilioService.sendMessage(phoneNumber, message);
            } else {
                twilioService.sendMessage(phoneNumber, "Could not retrieve weather information.");
            }
        } else {
            twilioService.sendMessage(phoneNumber, "Could not retrieve weather information.");
        }
    }
}
