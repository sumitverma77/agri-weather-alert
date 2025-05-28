package com.security.agriweatheralertsystem.enums;

public enum FallbackMessage {


        WEATHER_FETCH_FAILED_EN("Could not receive weather information."),
        WEATHER_FETCH_FAILED_HI("मौसम की जानकारी प्राप्त नहीं हो सकी"),
    LOCATION_UPDATE_SUCCESS_EN("Your Location updated to: %s. Now you will receive weather alerts for this location."),
    LOCATION_UPDATE_SUCCESS_HI("आपका स्थान अपडेट कर दिया गया है: %s. अब आपको इस स्थान के लिए मौसम अलर्ट प्राप्त होंगे।");

    private final String message;

    FallbackMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    }



