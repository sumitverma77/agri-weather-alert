package com.security.agriweatheralertsystem.service;

import java.util.Optional;

public interface AIService {
    Optional<String> getResponse(String prompt);
}
