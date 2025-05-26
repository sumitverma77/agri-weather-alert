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

    }
