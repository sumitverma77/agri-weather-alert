package com.security.agriweatheralertsystem.utils;

import com.security.agriweatheralertsystem.dto.WeatherDto;
import com.security.agriweatheralertsystem.enums.Language;


public class PromptBuilder {
    public static String IntentAndLocationPrompt(String userInput) {
        return """
                You are an NLP parser. Understand the user input in Hindi, English, or Hinglish.

                Your task is to:
                1. Identify the user's **intent** from these options:
                   - get_weather
                   - update_location
                   - none (if no intent matches)

                2. Extract the **location** in english if it is in hindi & if it is blank (leave empty "").

                3. Detect the **language** of the input:
                   - english (fully in English if not english then only check for hindi)
                   - hindi (fully in Hindi or mix of Hindi written in Latin script hinglish type)

                Return the result in english & strictly in this JSON format do not give any system output just give strict format as output:
                {
                  "intent": "<intent>",
                  "location": "<location or empty string>",
                  "language": "<english | hindi >"
                }

                ### Examples:
                        
                Input: "What is the weather in Ayodhya?"
                Output: { "intent": "get_weather", "location": "Ayodhya", "language": "english" }

                Input: "Update location to Delhi"
                Output: { "intent": "update_location", "location": "Delhi", "language": "english" }

                Input: "Hello bhai"
                Output: { "intent": "none", "location": "", "language": "hindi" }

                Input: "weather kya hai"
                Output: { "intent": "get_weather", "location": "", "language": "hindi" }
                                
                Input: "location ayodhya set kr do"
                Output: { "intent": "update_location", "location": "ayodhya", "language": "hindi" }
                                
                Input: "Ayodhya"
                Output: { "intent": "get_weather", "location": "ayodhya ", "language": "english" }
                 Input: "lucknow weather"
                Output: { "intent": "get_weather", "location": "Lucknow", "language": "english" }
                                

                Input: "%s"
                """.formatted(userInput);
    }

    public static String WeatherSummaryPrompt(WeatherDto data, String location, Language language) {
        WeatherDto.ForecastDay today = data.getForecast().getForecastday().get(0);
        WeatherDto.ForecastDay tomorrow = data.getForecast().getForecastday().get(1);
        return """
                Generate a concise, farmer-friendly weather summary (3-4 lines) for %s.
                Include:
                - Today's (%s) forecast: %s, Avg Temp: %s°C, Rain Chance: %s%%, Rainfall: %smm
                - Tomorrow's (%s) forecast: %s, Avg Temp: %s°C, Rain Chance: %s%%, Rainfall: %smm

                Give the response in %s language

                Also, include one tip for our farmers relevant to the weather.
                Do NOT include phrases like "Here is your response" or any system messages.
                Directly write the report as if it's ready to be sent to a user via WhatsApp or SMS.
                """.formatted(
                location,
                today.getDate(),
                today.getDay().getCondition().getText(),
                today.getDay().getAvgtempC(),
                today.getDay().getDailyChanceOfRain(),
                today.getDay().getTotalprecipMm(),

                tomorrow.getDate(),
                tomorrow.getDay().getCondition().getText(),
                tomorrow.getDay().getAvgtempC(),
                tomorrow.getDay().getDailyChanceOfRain(),
                tomorrow.getDay().getTotalprecipMm(),

                language
        );

    }


}
