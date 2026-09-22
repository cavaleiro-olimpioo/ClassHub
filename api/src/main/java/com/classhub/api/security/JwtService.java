package com.classhub.api.security;

import com.classhub.api.domain.ApiUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationHours;

    public JwtService(@Value("${app.jwt.secret:classhub-development-jwt-secret-change-me-before-production-2026}") String secret,
                      @Value("${app.jwt.expiration-hours:8}") long expirationHours) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationHours = expirationHours;
    }

    public String generate(ApiUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("perfil", user.getPerfil())
            .claim("email", user.getEmail())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS)))
            .signWith(key)
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
