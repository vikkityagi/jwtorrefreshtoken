package com.example.auth.controller;

import com.example.auth.config.JwtService;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.LoginResponse;
import com.example.auth.dto.RefreshTokenRequest;
import com.example.auth.entity.RefreshToken;
import com.example.auth.entity.User;
import com.example.auth.repository.RefreshTokenRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final RefreshTokenService refreshService;
    private final JwtService jwtService;

    // ✅ Register: create user only
    @PostMapping("/register")
    public String register(@RequestBody LoginRequest request) {
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            return "User already exists";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // later encrypt
        user.setRole("USER");

        userRepo.save(user);
        return "User registered successfully";
    }

    // ✅ Login: return access + refresh token
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ check password
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // ✅ generate tokens
        String accessToken = jwtService.generateToken(user.getUsername());
        RefreshToken refreshToken = refreshService.createRefreshToken(user.getId());

        return new LoginResponse(accessToken, refreshToken.getToken());
    }

    // ✅ Refresh token endpoint
    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody RefreshTokenRequest request) {

        RefreshToken token = refreshRepo.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshService.verifyExpiration(token);

        User user = userRepo.findById(token.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtService.generateToken(user.getUsername());

        return new LoginResponse(newAccessToken, token.getToken());
    }

    // ✅ Get authenticated user profile
    @GetMapping("/profile")
    public User getProfile() {
        // Get username from SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // Fetch user from DB
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
