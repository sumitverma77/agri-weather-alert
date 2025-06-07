package com.security.agriweatheralertsystem.scheduler;

import com.security.agriweatheralertsystem.entity.User;
import com.security.agriweatheralertsystem.repository.UserRepo;
import com.security.agriweatheralertsystem.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeatherAlertScheduler {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private WeatherService weatherService;

    // Run Everyday at 5 AM
    @Scheduled(cron = "0 0 5 * * ?")

    // run after 3 min
    // @Scheduled(cron = "0 0/3 * * * ?")
    public void sendWeatherAlerts() {
        List<User> users = userRepo.findAll();
        for (User user : users) {
            String phone = "whatsapp:" + user.getPhone();
            String location = user.getLocation();
            weatherService.sendWeatherAlert(phone, location);
        }
    }
}
