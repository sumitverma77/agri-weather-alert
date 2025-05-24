package com.security.agriweatheralertsystem.utils;

import java.util.Arrays;
import java.util.List;

public class LocationParser {

    public static String parseLocation(String messageBody) {
        String[] parts = messageBody.split("[,]");
        List<String> locations = Arrays.stream(parts)
                .map(String::trim)
                .toList();

        if (locations.size() == 1) {
            return locations.get(0);
        } else if (locations.size() == 2) {
            return locations.get(0); // Return the first location
        } else {
            return null;
        }
    }
}
