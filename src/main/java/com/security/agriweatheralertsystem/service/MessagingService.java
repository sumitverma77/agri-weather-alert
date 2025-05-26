package com.security.agriweatheralertsystem.service;

public interface MessagingService {

    void sendMessage(String phoneNumber, String message);
}
