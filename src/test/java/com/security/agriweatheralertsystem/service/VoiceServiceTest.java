package com.security.agriweatheralertsystem.service;

import com.security.agriweatheralertsystem.dto.WeatherDto;
import com.security.agriweatheralertsystem.enums.Language;
import com.security.agriweatheralertsystem.facade.WeatherApiFacade;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VoiceServiceTest {

    @Mock private WeatherApiFacade weatherApiFacade;
    @Mock private WeatherService weatherService;
    @Mock private HttpServletResponse httpServletResponse;
    @Mock private PrintWriter writer;

    @InjectMocks private VoiceService voiceService;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(httpServletResponse.getWriter()).thenReturn(writer);
    }

    @Test
    void testPromptLanguageSelection_generatesValidTwiml() throws Exception {
        voiceService.promptLanguageSelection(httpServletResponse);

        verify(writer, atLeastOnce()).write(contains("<Response>"));
        verify(httpServletResponse).setContentType("application/xml");
        verify(httpServletResponse).setCharacterEncoding("UTF-8");
    }

    @Test
    void testPromptCityName_withValidHindiInput() throws Exception {
        voiceService.promptCityName("1", "+921234567890", httpServletResponse);
        verify(writer, atLeastOnce()).write(contains("<Response>"));
    }

    @Test
    void testPromptCityName_withInvalidInput() throws Exception {
        voiceService.promptCityName("9", "+921234567890", httpServletResponse);

        verify(writer, atLeastOnce()).write(contains("<Response>"));
        verify(writer).write(contains("Your input is invalid"));
    }

    @Test
    void testProcessWeatherQuery_successfulFlow() throws Exception {
        String city = "Hyderabad", lang = "2", phone = "+921234567890";

        WeatherDto.Location location = new WeatherDto.Location(city);
        WeatherDto.Condition condition = new WeatherDto.Condition("Sunny");
        WeatherDto.Day day = new WeatherDto.Day(30.0, 10.0, 2.0, condition);
        WeatherDto.ForecastDay fd = new WeatherDto.ForecastDay("2024-06-17", day);
        WeatherDto.Forecast forecast = new WeatherDto.Forecast(Collections.singletonList(fd));
        WeatherDto mockDto = new WeatherDto(location, forecast);

        when(weatherApiFacade.getWeatherData(city)).thenReturn(Optional.of(mockDto));
        when(weatherService.summarize(any(), eq(city), eq(Language.ENGLISH)))
                .thenReturn(Optional.of("Sunny day expected."));

        voiceService.processWeatherQuery(city, lang, phone, httpServletResponse);

        verify(writer, atLeastOnce()).write(contains("Sunny day expected."));
        verify(httpServletResponse).setContentType("application/xml");
    }

    @Test
    void testProcessWeatherQuery_invalidLocation() throws Exception {
        voiceService.processWeatherQuery(" ", "2", "+921234567890", httpServletResponse);

        verify(writer).write(contains("Your input is invalid"));
        verify(writer).write(contains("call is ending"));
    }

    @Test
    void testProcessWeatherQuery_exceptionFromApi() throws Exception {
        String city = "Hyderabad", lang = "1", phone = "+921234567890";

        when(weatherApiFacade.getWeatherData(city)).thenThrow(new RuntimeException("API Down"));

        voiceService.processWeatherQuery(city, lang, phone, httpServletResponse);

        verify(writer, atLeastOnce()).write(contains("मौसम सेवा उपलब्ध नहीं है"));
    }

    @Test
    void testUpdateUserPreferenceFlow_digitOne() throws Exception {
        voiceService.updateUserPreferenceFlow("1", "Hyderabad", "1", "+921234567890", httpServletResponse);

        verify(weatherService).updateUserPreferences("+921234567890", "Hyderabad", Language.HINDI);
        verify(writer).write(contains("सफलतापूर्वक अपडेट हो गया"));
    }


    @Test
    void testUpdateUserPreferenceFlow_otherDigit() throws Exception {
        voiceService.updateUserPreferenceFlow("2", "Hyderabad", "2", "+921234567890", httpServletResponse);

        verify(weatherService).updateUserPreferences("+921234567890", "Hyderabad", Language.ENGLISH);
        verify(writer).write(contains("successfully updated"));
    }

}
