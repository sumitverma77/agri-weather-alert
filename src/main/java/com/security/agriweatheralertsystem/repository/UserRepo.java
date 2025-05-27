package com.security.agriweatheralertsystem.repository;

import com.security.agriweatheralertsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
    User findByPhone(String phone);
}
