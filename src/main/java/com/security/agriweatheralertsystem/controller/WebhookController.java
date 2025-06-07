package com.security.agriweatheralertsystem.controller;

import com.security.agriweatheralertsystem.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WebhookController {

    @Autowired
    private WeatherService weatherService;


    @PostMapping("/webhook")
    public void receiveMessage(
            @RequestParam("From") String phoneNumber,
            @RequestParam("Body") String messageBody) {
        weatherService.sendWeatherAlert(phoneNumber, messageBody);
    }

}
