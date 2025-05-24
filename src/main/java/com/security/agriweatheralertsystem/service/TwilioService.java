package com.security.agriweatheralertsystem.service;

public interface TwilioService {

    void sendMessage(String phoneNumber, String message);
}
