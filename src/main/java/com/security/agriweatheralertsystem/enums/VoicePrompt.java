package com.security.agriweatheralertsystem.enums;

public enum VoicePrompt {
    LANGUAGE_SELECTION(
            "हिंदी के लिए १ दबाएँ। For English Press 2",
            "िंदी के लिए १ दबाएँ।  Press 2 for English."
    ),
    NO_INPUT(
            "कोई इनपुट नहीं मिला। धन्यवाद! No input received. Thank you!",
            "कोई इनपुट नहीं मिला। धन्यवाद! No input received. Thank you!"
    ),
    CITY_NAME_REQUEST(
            "जिस शहर का मौसम आप जानना चाहते हैं, कृपया उसका नाम बोलें।",
            "Please say the name of the city whose weather you want to know."
    ),
    INVALID_INPUT(
            "आपका इनपुट मान्य नहीं है। कॉल समाप्त की जा रही है। धन्यवाद!",
            "Your input is invalid. The call is ending. Thank you!"
    ),
    SERVICE_UNAVAILABLE(
            " मौसम सेवा उपलब्ध नहीं है। कृपया बाद में पुनः प्रयास करें।",
            "Weather service is currently unavailable. Please try again later."
    ),
    CITY_UPDATE_REQUEST(
            "यदि आप दैनिक अलर्ट के लिए अपना स्थान अपडेट करना चाहते हैं, तो 1 दबाएँ। कॉल समाप्त करने के लिए कोई भी अन्य कुंजी दबाएँ।",
            "If you want to update your location for daily alerts, press 1. Press any other key to end the call."
    ),
    CITY_UPDATE_SUCCESS(
            "आपका शहर सफलतापूर्वक अपडेट हो गया है। अब आप इस शहर के मौसम अलर्ट प्राप्त करेंगे।",
            "Your city has been successfully updated. You will now receive weather alerts for this city."
    ),
    GOOD_BYE(
            "धन्यवाद! कॉल समाप्त हो रहा है।",
            "Thank you! Ending the call."
    );

    private final String hindi;
    private final String english;

    VoicePrompt(String hindi, String english) {
        this.hindi = hindi;
        this.english = english;
    }

    public String getPrompt(Language language) {
        return switch (language) {
            case HINDI -> hindi;
            case ENGLISH -> english;
        };
    }
}
