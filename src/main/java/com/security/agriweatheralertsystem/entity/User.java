package com.security.agriweatheralertsystem.entity;

import com.security.agriweatheralertsystem.enums.Language;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    String id;
    String phone;
    String location;
    @Enumerated(EnumType.STRING)
    private Language language;
}
