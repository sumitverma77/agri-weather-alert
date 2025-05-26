package com.security.agriweatheralertsystem.enums;

public enum FallbackMessage {


        WEATHER_FETCH_FAILED_EN("Could not receive weather information."),
        WEATHER_FETCH_FAILED_HI("मौसम की जानकारी प्राप्त नहीं हो सकी");

        private final String message;

        FallbackMessage(String message) {
            this.message = message;
        }

        public static String getMessage(Language language) {
            return switch (language) {
                case ENGLISH -> WEATHER_FETCH_FAILED_EN.message;
                case HINDI -> WEATHER_FETCH_FAILED_HI.message;
            };
        }
    }



