package com.security.agriweatheralertsystem.controller;

import com.security.agriweatheralertsystem.service.WeatherService;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Say;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

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
