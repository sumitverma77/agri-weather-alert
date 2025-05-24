package com.security.agriweatheralertsystem.service.impl;

import com.security.agriweatheralertsystem.service.LocationService;
import com.security.agriweatheralertsystem.utils.LocationParser;
import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl implements LocationService {

    @Override
    public String parseLocation(String messageBody) {
        return LocationParser.parseLocation(messageBody);
    }
}
