package com.security.agriweatheralertsystem.enums;

public enum FallbackMessage {
    WEATHER_FETCH_FAILED(
            "Could not receive weather information.",
            "मौसम की जानकारी प्राप्त नहीं हो सकी"),
    LOCATION_UPDATE_SUCCESS(
            "Your Location updated to: %s. Now you will receive weather alerts for this location.",
            "आपका स्थान अपडेट कर दिया गया है: %s. अब आपको इस स्थान के लिए मौसम अलर्ट प्राप्त होंगे।"),
    INTERNAL_ERROR_RESPONSE_FAILED(
            "Could not process your request due to an internal error. Please try again later.",
            "आंतरिक त्रुटि के कारण आपका अनुरोध संसाधित नहीं किया जा सका। कृपया बाद में पुनः प्रयास करें।"),
    UNKNOWN_REQUEST(
            "Couldn't understand your request. Please try again.",
            "आपका अनुरोध समझ नहीं आया। कृपया पुनः प्रयास करें।"),
    WEATHER_INFO_UNAVAILABLE(
            "Weather information is currently unavailable. Please try again later.",
            "मौसम की जानकारी वर्तमान में उपलब्ध नहीं है। कृपया बाद में पुनः प्रयास करें."
    );
    private final String englishMessage;
    private final String hindiMessage;

    FallbackMessage(String englishMessage, String hindiMessage) {
        this.englishMessage = englishMessage;
        this.hindiMessage = hindiMessage;
    }

    public String getMessage(Language language) {
        return switch (language) {
            case ENGLISH -> englishMessage;
            case HINDI -> hindiMessage;
        };
    }
}
