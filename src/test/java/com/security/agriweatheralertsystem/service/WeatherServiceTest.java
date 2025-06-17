package com.security.agriweatheralertsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.agriweatheralertsystem.constant.Constants;
import com.security.agriweatheralertsystem.dto.ParsedMessage;
import com.security.agriweatheralertsystem.dto.WeatherDto;
import com.security.agriweatheralertsystem.entity.User;
import com.security.agriweatheralertsystem.enums.Language;
import com.security.agriweatheralertsystem.facade.WeatherApiFacade;
import com.security.agriweatheralertsystem.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    private WeatherService weatherService;

    @Mock private AIService aiService;
    @Mock private MessagingService messagingService;
    @Mock private UserRepo userRepo;
    @Mock private RestTemplate restTemplate;
    @Mock private WeatherApiFacade weatherApiFacade;
    @Mock private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherService = spy(new WeatherService());

        injectPrivateField(weatherService, "aiService", aiService);
        injectPrivateField(weatherService, "messagingService", messagingService);
        injectPrivateField(weatherService, "userRepo", userRepo);
        injectPrivateField(weatherService, "restTemplate", restTemplate);
        injectPrivateField(weatherService, "weatherApiFacade", weatherApiFacade);
        injectPrivateField(weatherService, "mapper", mapper);
    }


    @Test
    void testSummarize_returnsExpectedSummary() {

        WeatherDto dto = mock(WeatherDto.class);
        WeatherDto.Forecast forecast = mock(WeatherDto.Forecast.class);
        WeatherDto.ForecastDay forecastDay1 = mock(WeatherDto.ForecastDay.class);
        WeatherDto.ForecastDay forecastDay2 = mock(WeatherDto.ForecastDay.class);
        WeatherDto.Day day1 = mock(WeatherDto.Day.class);
        WeatherDto.Day day2 = mock(WeatherDto.Day.class);
        WeatherDto.Condition condition1 = mock(WeatherDto.Condition.class);
        WeatherDto.Condition condition2 = mock(WeatherDto.Condition.class);


        when(dto.getForecast()).thenReturn(forecast);
        when(forecast.getForecastday()).thenReturn(List.of(forecastDay1, forecastDay2));
        when(forecastDay1.getDay()).thenReturn(day1);
        when(forecastDay2.getDay()).thenReturn(day2);


        when(day1.getCondition()).thenReturn(condition1);
        when(day2.getCondition()).thenReturn(condition2);
        when(condition1.getText()).thenReturn("Sunny");
        when(condition2.getText()).thenReturn("Cloudy");


        when(aiService.getResponse(anyString())).thenReturn(Optional.of("It will be sunny."));


        Optional<String> summary = weatherService.summarize(dto, "Hyderabad", Language.ENGLISH);

        assertTrue(summary.isPresent());
        assertEquals("It will be sunny.", summary.get());
    }



    @Test
    void testUpdateUserPreferences_newUser_saved() {
        String phone = "whatsapp:+921234567890";
        String cleanPhone = "+921234567890";
        String city = "Hyderabad";
        Language lang = Language.HINDI;

        when(userRepo.findByPhone(cleanPhone)).thenReturn(Optional.empty());

        weatherService.updateUserPreferences(phone, city, lang);

        verify(userRepo).save(argThat(user ->
                user.getPhone().equals(cleanPhone)
                        && user.getLocation().equals(city)
                        && user.getLanguage().equals(lang)
        ));
    }

    @Test
    void testHandleWeatherRequest_updateLocation_success() {
        String msgBody = "update Hyderabad Hindi";
        String phone = "+921234567890";
        ParsedMessage parsed = new ParsedMessage("update_location", "Hyderabad", "Hindi");

        doReturn(parsed).when(weatherService).parseMessage(msgBody);

        Optional<String> response = weatherService.handleWeatherRequest(phone, msgBody);

        assertTrue(response.isPresent());
        assertFalse(response.get().isBlank());
    }

    @Test
    void testSendWeatherAlert_sendsMessage() {
        String msgBody = "weather Hyderabad English";
        String phone = "+921234567890";
        doReturn(Optional.of("Sunny forecast")).when(weatherService).handleWeatherRequest(phone, msgBody);

        weatherService.sendWeatherAlert(phone, msgBody);

        verify(messagingService).sendMessage(phone, "Sunny forecast");
    }

    @Test
    void testParseMessage_correctlyParsesFromAI() {
        String body = "Get weather for Lahore in English";
        Map<String, String> mockDetails = Map.of(
                Constants.INTENT, "get_weather",
                Constants.LOCATION, "Lahore",
                Constants.LANGUAGE, "English"
        );

        doReturn(mockDetails).when(weatherService).extractDetailsWithAI(anyString());

        ParsedMessage parsed = weatherService.parseMessage(body);

        assertEquals("get_weather", parsed.getIntent());
        assertEquals("Lahore", parsed.getLocation());
        assertEquals("English", parsed.getLanguage());
    }
    private void injectPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
