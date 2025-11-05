package com.example.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password; // optional after first login
    private String accessToken; // send only for login
}
