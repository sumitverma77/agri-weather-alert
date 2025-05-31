package com.security.agriweatheralertsystem.enums;

public enum IntentType {
    GET_WEATHER("get_weather"),
    UPDATE_LOCATION("update_location");
    private final String value;

    IntentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
