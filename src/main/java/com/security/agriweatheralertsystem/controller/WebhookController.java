package com.security.agriweatheralertsystem.controller;

import com.security.agriweatheralertsystem.dto.WeatherDTO;
import com.security.agriweatheralertsystem.service.LocationService;
import com.security.agriweatheralertsystem.service.TwilioService;
import com.security.agriweatheralertsystem.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WebhookController {

    private final TwilioService twilioService;
    private final WeatherService weatherService;
    private final LocationService locationService;

    @Autowired
    public WebhookController(TwilioService twilioService, WeatherService weatherService, LocationService locationService) {
        this.twilioService = twilioService;
        this.weatherService = weatherService;
        this.locationService = locationService;
    }

    @PostMapping("/webhook")
    public void receiveMessage(
            @RequestParam("From") String phoneNumber,
            @RequestParam("Body") String messageBody) {

        // Log the message
        System.out.println("Received message from: " + phoneNumber);
        System.out.println("Message body: " + messageBody);

        String city = locationService.parseLocation(messageBody);
        WeatherDTO weatherData = weatherService.getWeatherData(city);

        if (weatherData != null) {
            String weatherSummary = "Current weather in " + weatherData.getCityName() + ": " + weatherData.getConditionText() + ", " + weatherData.getTemperatureC() + "°C";
            System.out.println("Weather Summary: " + weatherSummary);

            twilioService.sendMessage(phoneNumber, weatherSummary);
        } else {
            twilioService.sendMessage(phoneNumber, "Could not retrieve weather information for " + city);
        }
    }
}
