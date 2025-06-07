package com.security.agriweatheralertsystem.controller;

import com.security.agriweatheralertsystem.service.VoiceService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
public class VoiceController {
    @Autowired
    private VoiceService voiceService;


    @PostMapping(value = "/voice", produces = MediaType.APPLICATION_XML_VALUE)
    public void handleInitialCall(HttpServletResponse response) throws IOException {
        log.info("Received initial call");
        voiceService.promptLanguageSelection(response);
    }

    @PostMapping(value = "/language-selection", produces = MediaType.APPLICATION_XML_VALUE)
    public void handleLanguageSelection(@RequestParam("Digits") String digits,
                                        @RequestParam("From") String caller,
                                        HttpServletResponse response) throws IOException {
        log.info("Language selection by {}", caller);
        voiceService.promptCityName(digits, caller, response);
    }

    @PostMapping(value = "/voice-input", produces = MediaType.APPLICATION_XML_VALUE)
    public void handleVoiceInput(@RequestParam("SpeechResult") String city,
                                 @RequestParam("lang") String lang,
                                 @RequestParam("from") String phone,
                                 HttpServletResponse response) throws IOException {
        log.info("Received speech input: '{}', lang: {}, phone: {}", city, lang, phone);

        voiceService.processWeatherQuery(city, lang, phone, response);
    }

    @PostMapping(value = "/update-preference", produces = MediaType.APPLICATION_XML_VALUE)
    public void handlePreferenceUpdateChoice(@RequestParam("Digits") String digits,
                                             @RequestParam("city") String city,
                                             @RequestParam("lang") String lang,
                                             @RequestParam("from") String phone,
                                             HttpServletResponse response) throws IOException {
        voiceService.updateUserPreferenceFlow(digits, city, lang, phone, response);
    }

}
