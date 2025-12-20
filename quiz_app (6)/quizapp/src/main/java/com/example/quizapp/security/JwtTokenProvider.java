package com.example.quizapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret; // Base64-encoded key

    private final long accessValidityMs = 15 * 60 * 1000L;  // 15 минут
    private final long refreshValidityMs = 7 * 24 * 60 * 60 * 1000L; // 7 дней

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)                    // <-- ЭТОТ МЕТОД ЕСТЬ!
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessValidityMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)                    // <-- И ЗДЕСЬ ТОЖЕ!
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshValidityMs))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, String expectedType) {
        try {
            Claims claims = getClaims(token);
            return claims.get("type", String.class).equals(expectedType)
                    && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}