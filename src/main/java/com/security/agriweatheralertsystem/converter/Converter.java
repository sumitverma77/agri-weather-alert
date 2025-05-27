package com.security.agriweatheralertsystem.converter;

import com.security.agriweatheralertsystem.entity.User;

public class Converter {
     public static User toUserEntity(String phone, String location) {
           User user = new User();
           user.setPhone(phone);
           user.setLocation(location);
return user;
     }

}
