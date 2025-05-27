package com.security.agriweatheralertsystem.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherAlertScheduler {
    // Runs every 2 minutes
    @Scheduled(cron = "0 */2 * * * *")
    public void sendWeatherAlerts() {

    }
}
