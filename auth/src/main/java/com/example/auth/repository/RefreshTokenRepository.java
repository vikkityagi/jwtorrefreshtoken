package com.example.auth.repository;

import com.example.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // ✅ Find by token string (used during refresh)
    Optional<RefreshToken> findByToken(String token);

    // ✅ Find refresh token by userId (used during login token check)
    Optional<RefreshToken> findByUserId(Long userId);
}
