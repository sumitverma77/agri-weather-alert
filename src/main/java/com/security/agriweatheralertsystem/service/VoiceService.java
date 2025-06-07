package com.security.agriweatheralertsystem.service;

import com.security.agriweatheralertsystem.dto.WeatherDto;
import com.security.agriweatheralertsystem.enums.Language;
import com.security.agriweatheralertsystem.enums.VoicePrompt;
import com.security.agriweatheralertsystem.facade.WeatherApiFacade;
import com.twilio.http.HttpMethod;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Say;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class VoiceService {

    @Autowired
    private WeatherApiFacade weatherApiFacade; // For weather data fetch if needed
    @Autowired
    private WeatherService weatherService;

    private static final String LANGUAGE_SELECTION_ENDPOINT = "/api/language-selection";
    private static final String CITY_UPDATE_ENDPOINT = "/api/update-preference?city=%s&lang=%s&from=%s";
    public static final String VOICE_INPUT_URL_FORMAT = "/api/voice-input?lang=%s&from=%s";
    private static final String DIGIT_ONE = "1";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CHARACTER_ENCODING_UTF8 = "UTF-8";


    public void promptLanguageSelection(HttpServletResponse response) throws IOException {
        String prompt = VoicePrompt.LANGUAGE_SELECTION.getPrompt(Language.ENGLISH);
        Gather gather = new Gather.Builder()
                .inputs(Collections.singletonList(Gather.Input.DTMF))
                .numDigits(1)
                .timeout(5)
                .action(LANGUAGE_SELECTION_ENDPOINT)
                .method(HttpMethod.POST)
                .say(new Say.Builder(prompt)
                        .voice(Say.Voice.POLLY_ADITI)
                        .language(Say.Language.HI_IN)
                        .build())
                .build();

        VoiceResponse twiml = new VoiceResponse.Builder()
                .gather(gather)
                .say(new Say.Builder(VoicePrompt.LANGUAGE_SELECTION.getPrompt(Language.ENGLISH))
                        .voice(Say.Voice.POLLY_ADITI)
                        .language(Say.Language.HI_IN)
                        .build())
                .build();

        writeResponse(response, twiml);
    }

    public void promptCityName(String digits, String caller, HttpServletResponse response) throws IOException {
        Say.Language sayLanguage;
        Gather.Language gatherLanguage;
        String prompt;

        if (DIGIT_ONE.equals(digits)) {
            sayLanguage = Say.Language.HI_IN;
            gatherLanguage = Gather.Language.EN_IN;
            prompt = VoicePrompt.CITY_NAME_REQUEST.getPrompt(Language.HINDI);
        } else if ("2".equals(digits)) {
            sayLanguage = Say.Language.EN_IN;
            gatherLanguage = Gather.Language.EN_IN;
            prompt = VoicePrompt.CITY_NAME_REQUEST.getPrompt(Language.ENGLISH);
        } else {
            VoiceResponse twiml = new VoiceResponse.Builder()
                    .say(new Say.Builder(VoicePrompt.INVALID_INPUT.getPrompt(Language.ENGLISH))
                            .voice(Say.Voice.POLLY_ADITI)
                            .language(Say.Language.HI_IN)
                            .build())
                    .build();
            writeResponse(response, twiml);
            return;
        }

        Gather gather = new Gather.Builder()
                .inputs(Collections.singletonList(Gather.Input.SPEECH))
                .timeout(5)
                .language(gatherLanguage)
                .action(String.format(VOICE_INPUT_URL_FORMAT, digits, URLEncoder.encode(caller, StandardCharsets.UTF_8)))
                .method(HttpMethod.POST)
                .say(new Say.Builder(prompt)
                        .voice(Say.Voice.POLLY_ADITI)
                        .language(sayLanguage)
                        .build())
                .build();

        VoiceResponse twiml = new VoiceResponse.Builder()
                .gather(gather)
                .say(new Say.Builder(VoicePrompt.CITY_NAME_REQUEST.getPrompt(Language.ENGLISH))
                        .voice(Say.Voice.POLLY_ADITI)
                        .language(sayLanguage)
                        .build())
                .build();

        writeResponse(response, twiml);
    }


    public void processWeatherQuery(String location, String lang, String phone, HttpServletResponse response) throws IOException {
        Language summarylanguage = DIGIT_ONE.equals(lang) ? Language.HINDI : Language.ENGLISH;
        location = location.trim();
        if (location.isEmpty() || location.contains("?")) {
            log.warn("Invalid location input: '{}'", location);
            VoiceResponse twiml = new VoiceResponse.Builder()
                    .say(new Say.Builder(VoicePrompt.INVALID_INPUT.getPrompt(summarylanguage))
                            .voice(Say.Voice.POLLY_ADITI)
                            .language(Say.Language.EN_IN)
                            .build())
                    .build();
            writeResponse(response, twiml);
        }
        Say.Language language = DIGIT_ONE.equals(lang) ? Say.Language.HI_IN : Say.Language.EN_IN;
        log.info("Processing weather query for location: {}, language: {}, phone: {}", location, lang, phone);

        Optional<WeatherDto> weatherData;
        try {
            weatherData = weatherApiFacade.getWeatherData(location);
            log.info(weatherData.isPresent() ? "Weather data fetched successfully." : "Failed to fetch weather data for location: {}", location);
        } catch (Exception e) {
            log.error("Exception while fetching weather data for location: {}", location, e);

            VoiceResponse fallback = new VoiceResponse.Builder()
                    .say(new Say.Builder(VoicePrompt.SERVICE_UNAVAILABLE.getPrompt(summarylanguage))
                            .voice(Say.Voice.POLLY_ADITI)
                            .language(language)
                            .build())
                    .build();
            writeResponse(response, fallback);
            return;
        }


        // Now you're outside the try-catch, and weatherData is definitely initialized
        String summary = weatherService.summarize(weatherData.orElse(null), location, summarylanguage)
                .orElse(VoicePrompt.SERVICE_UNAVAILABLE.getPrompt(summarylanguage));
        log.info("Weather summary generated: {}", summary);

        // Main TwiML response
        VoiceResponse.Builder twimlBuilder = new VoiceResponse.Builder()
                .say(new Say.Builder(summary)
                        .voice(Say.Voice.POLLY_ADITI)
                        .language(language)
                        .build());

        String promptUpdate = DIGIT_ONE.equals(lang) ?
                VoicePrompt.CITY_UPDATE_REQUEST.getPrompt(Language.HINDI) :
                VoicePrompt.CITY_UPDATE_REQUEST.getPrompt(Language.ENGLISH);
        String promptGoodbye = DIGIT_ONE.equals(lang) ? VoicePrompt.GOOD_BYE.getPrompt(Language.HINDI) :
                VoicePrompt.GOOD_BYE.getPrompt(Language.ENGLISH);
        Gather gather = new Gather.Builder()
                .inputs(Collections.singletonList(Gather.Input.DTMF))
                .numDigits(1)
                .timeout(5)
                .action(String.format(CITY_UPDATE_ENDPOINT,
                        URLEncoder.encode(location, StandardCharsets.UTF_8),
                        lang,
                        URLEncoder.encode(phone, StandardCharsets.UTF_8)))
                .method(HttpMethod.POST)
                .say(new Say.Builder(promptUpdate)
                        .voice(Say.Voice.POLLY_ADITI)
                        .language(language)
                        .build())
                .build();

        twimlBuilder.gather(gather);

        twimlBuilder.say(new Say.Builder(promptGoodbye)
                .voice(Say.Voice.POLLY_ADITI)
                .language(language)
                .build());

        writeResponse(response, twimlBuilder.build());
    }

    public void updateUserPreferenceFlow(String digits, String city, String lang, String phone, HttpServletResponse response) throws IOException {
        Say.Language language = DIGIT_ONE.equals(lang) ? Say.Language.HI_IN : Say.Language.EN_IN;
        String reply = DIGIT_ONE.equals(lang) ?
                VoicePrompt.CITY_UPDATE_SUCCESS.getPrompt(Language.HINDI) :
                VoicePrompt.CITY_UPDATE_SUCCESS.getPrompt(Language.ENGLISH);

        if (DIGIT_ONE.equals(digits)) {
            weatherService.updateUserPreferences(phone, city, Language.fromString(lang));
        }
        VoiceResponse twiml = new VoiceResponse.Builder()
                .say(new Say.Builder(reply)
                        .voice(Say.Voice.POLLY_ADITI)
                        .language(language)
                        .build())
                .hangup(new Hangup.Builder().build())
                .build();

        writeResponse(response, twiml);

    }

    private void writeResponse(HttpServletResponse response, VoiceResponse twiml) throws IOException {
        try {
            response.setContentType(CONTENT_TYPE_XML);
            response.setCharacterEncoding(CHARACTER_ENCODING_UTF8);
            String xml = twiml.toXml();
            log.info("Generated TwiML:\n{}", xml);
            response.getWriter().write(xml);
        } catch (Exception e) {
            log.error("Error writing TwiML response: {}", e.getMessage(), e);
        }
    }
}
