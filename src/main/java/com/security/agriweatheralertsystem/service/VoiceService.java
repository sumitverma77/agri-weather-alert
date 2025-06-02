package com.security.agriweatheralertsystem.service;

import com.security.agriweatheralertsystem.dto.WeatherDto;
import com.security.agriweatheralertsystem.enums.Language;
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


    public void promptLanguageSelection(HttpServletResponse response) throws IOException {
        String prompt = "Hindi ke liye ek dabayen. For English press 2.";

        Gather gather = new Gather.Builder()
                .inputs(Collections.singletonList(Gather.Input.DTMF))
                .numDigits(1)
                .timeout(5)
                .action("/api/language-selection")
                .method(HttpMethod.POST)
                .say(new Say.Builder(prompt)
                        .voice(Say.Voice.POLLY_ADITI)
                        .language(Say.Language.HI_IN)
                        .build())
                .build();

        VoiceResponse twiml = new VoiceResponse.Builder()
                .gather(gather)
                .say(new Say.Builder("Koi input nahi mila. Dhanyawaad!")
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

        if ("1".equals(digits)) {
            sayLanguage = Say.Language.HI_IN;
            gatherLanguage = Gather.Language.EN_IN;
            prompt = "Apne sheher ka naam bolein jiska mausam aap jaan na chahte hain.";
        } else if ("2".equals(digits)) {
            sayLanguage = Say.Language.EN_IN;
            gatherLanguage = Gather.Language.EN_IN;
            prompt = "Please say the name of your city to get the weather update.";
        } else {
            VoiceResponse twiml = new VoiceResponse.Builder()
                    .say(new Say.Builder("Galat vikalp. Call samapt ho raha hai. Dhanyawaad!")
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
                .action("/api/voice-input?lang=" + digits + "&from=" + URLEncoder.encode(caller, StandardCharsets.UTF_8))
                .method(HttpMethod.POST)
                .say(new Say.Builder(prompt)
                        .voice(Say.Voice.POLLY_ADITI)
                        .language(sayLanguage)
                        .build())
                .build();

        VoiceResponse twiml = new VoiceResponse.Builder()
                .gather(gather)
                .say(new Say.Builder("Koi input nahi mila. Dhanyawaad!")
                        .voice(Say.Voice.POLLY_ADITI)
                        .language(sayLanguage)
                        .build())
                .build();

        writeResponse(response, twiml);
    }


    public void processWeatherQuery(String location, String lang, String phone, HttpServletResponse response) throws IOException {
        Say.Language language = "1".equals(lang) ? Say.Language.HI_IN : Say.Language.EN_IN;
        log.info("Processing weather query for location: {}, language: {}, phone: {}", location, lang, phone);

        Optional<WeatherDto> weatherData;
        try {
            weatherData = weatherApiFacade.getWeatherData(location);
            log.info(weatherData.isPresent() ? "Weather data fetched successfully." : "Failed to fetch weather data for location: {}", location);
        } catch (Exception e) {
            log.error("Exception while fetching weather data for location: {}", location, e);

            VoiceResponse fallback = new VoiceResponse.Builder()
                    .say(new Say.Builder("Weather service is currently facing issues. Please try again later.")
                            .voice(Say.Voice.POLLY_ADITI)
                            .language(language)
                            .build())
                    .build();
            writeResponse(response, fallback);
            return;
        }

        Language summarylanguage = "1".equals(lang) ? Language.HINDI : Language.ENGLISH;

        // Now you're outside the try-catch, and weatherData is definitely initialized
        String summary = weatherService.summarize(weatherData.orElse(null), location, summarylanguage)
                .orElse("Weather information is currently unavailable. Please try again later.");
        log.info("Weather summary generated: {}", summary);

        // Main TwiML response
        VoiceResponse.Builder twimlBuilder = new VoiceResponse.Builder()
                .say(new Say.Builder(summary)
                        .voice(Say.Voice.POLLY_ADITI)
                        .language(language)
                        .build());

        String promptUpdate = "If you want to update your location for daily alerts, press 1. To end the call, press any other key.";
        Gather gather = new Gather.Builder()
                .inputs(Collections.singletonList(Gather.Input.DTMF))
                .numDigits(1)
                .timeout(5)
                .action(String.format("/api/update-preference?city=%s&lang=%s&from=%s",
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

        twimlBuilder.say(new Say.Builder("Thank you for calling. Goodbye!")
                .voice(Say.Voice.POLLY_ADITI)
                .language(language)
                .build());

        writeResponse(response, twimlBuilder.build());
    }


    public void updateUserPreferenceFlow(String digits, String city, String lang, String phone, HttpServletResponse response) throws IOException {
        Say.Language language = "1".equals(lang) ? Say.Language.HI_IN : Say.Language.EN_IN;

        if ("1".equals(digits)) {
            // Call your existing service to update preferences

            weatherService.updateUserPreferences(phone, city, Language.fromString(lang));
            String reply = (language == Say.Language.HI_IN) ? "Aapka location aur language update kar diya gaya hai. Dhanyawaad!" : "Your location and language have been updated. Thank you!";

            VoiceResponse twiml = new VoiceResponse.Builder()
                    .say(new Say.Builder(reply)
                            .voice(Say.Voice.POLLY_ADITI)
                            .language(language)
                            .build())
                    .hangup(new Hangup.Builder().build())
                    .build();

            writeResponse(response, twiml);
        } else {
            VoiceResponse twiml = new VoiceResponse.Builder()
                    .say(new Say.Builder(language == Say.Language.HI_IN ? "Dhanyawaad! Call samapt ho raha hai." : "Thank you! Ending the call.")
                            .voice(Say.Voice.POLLY_ADITI)
                            .language(language)
                            .build())
                    .hangup(new Hangup.Builder().build())
                    .build();

            writeResponse(response, twiml);
        }
    }

    private void writeResponse(HttpServletResponse response, VoiceResponse twiml) throws IOException {
        try {
            response.setContentType("application/xml");
            response.setCharacterEncoding("UTF-8"); // ✅ Important to handle Hindi characters
            String xml = twiml.toXml();
            log.info("Generated TwiML:\n{}", xml); // ✅ Log the exact response
            response.getWriter().write(xml);
        } catch (Exception e) {
            log.error("Error writing TwiML response: {}", e.getMessage(), e);
        }
    }
}
