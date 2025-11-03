package com.example.auth.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class RefreshToken {
    @Id @GeneratedValue
    private Long id;

    private Long userId;
    private String token;
    private Instant expiryDate;
}
