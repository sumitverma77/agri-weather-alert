package com.security.agriweatheralertsystem.converter;

import com.security.agriweatheralertsystem.entity.User;

import java.util.Optional;

public class Converter {
     public static Optional<User> toUserEntity(String phone, String location) {
           User user = new User();
           user.setPhone(phone);
           user.setLocation(location);
return Optional.of(user);
     }

}
