package com.security.agriweatheralertsystem.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {
    public static String cleanJson(String rawJson) {
        String json = rawJson.trim();

        if (json.startsWith("Optional[")) {
            json = json.substring("Optional[".length(), json.length() - 1).trim();
        }

        return json
                .replaceAll("(?i)^```json", "")
                .replaceAll("^```", "")
                .replaceAll("```$", "")
                .trim();
    }

}
