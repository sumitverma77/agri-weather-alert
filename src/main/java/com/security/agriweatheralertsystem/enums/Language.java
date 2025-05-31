package com.security.agriweatheralertsystem.enums;

public enum Language {
    ENGLISH("English"),
    HINDI("Hindi");
    private final String language;

    Language(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public static Language fromString(String lang) {
        return switch (lang.toLowerCase()) {
            case "english" -> ENGLISH;
            case "hindi" -> HINDI;
            default -> ENGLISH; // fallback
        };
    }

    }
