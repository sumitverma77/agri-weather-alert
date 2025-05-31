package com.security.agriweatheralertsystem.converter;

import com.security.agriweatheralertsystem.entity.User;
import com.security.agriweatheralertsystem.enums.Language;

public class Converter {
    public static User toUserEntity(String phone, String location, Language language) {
        User user = new User();
        user.setPhone(phone);
        user.setLocation(location);
        user.setLanguage(language);
        return user;
    }

}
