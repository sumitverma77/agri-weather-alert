package com.security.agriweatheralertsystem.converter;

import com.security.agriweatheralertsystem.entity.User;
import com.security.agriweatheralertsystem.enums.Language;

import java.util.Optional;

public class Converter {
     public static Optional<User> toUserEntity(String phone, String location , Language language) {
           User user = new User();
           user.setPhone(phone);
           user.setLocation(location);
           user.setLanguage(language);
return Optional.of(user);
     }

}
