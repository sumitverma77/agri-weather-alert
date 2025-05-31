package com.security.agriweatheralertsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParsedMessage {
    private String intent;
    private String location;
    private String language;
}
