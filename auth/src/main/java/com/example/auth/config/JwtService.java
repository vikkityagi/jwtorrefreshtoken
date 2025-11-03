package com.example.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // ✅ Generate a strong secret key automatically
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final long JWT_EXPIRATION = 1 * 60 * 1000; // 15 min

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(key) // ✅ key must be 256 bits or more
                .compact();
    }

    public String validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return claims.getBody().getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired"); // ✅ Token expired error
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token");
        }
    }
}
