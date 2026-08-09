package org.bank.authcommon.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final Key signingKey;
    private final long accessTokenExpiration;

    public JwtService(
            @Value("${secret-key}") String secret,
            @Value("${jwt.access.expiration:3600000}") long accessTokenExpiration
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public String createAccessToken(Long userId,
                                    String email,
                                    String role) {

        return createToken(
                userId.toString(),
                Map.of(
                        "email", email,
                        "role", role
                )
        );
    }

    public String createServiceToken(String serviceName) {

        return createToken(
                serviceName,
                Map.of(
                        "service", serviceName,
                        "role", "SERVICE"
                )
        );
    }

    private String createToken(String subject,
                               Map<String, Object> claims) {

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenExpiration)))
                .signWith(signingKey)
                .compact();
    }

    public boolean validateToken(String token) {

        try {
            Claims claims = extractAllClaims(token);

            return claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractService(String token) {
        return extractClaim(token, claims -> claims.get("service", String.class));
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token,
                              Function<Claims, T> resolver) {

        return resolver.apply(extractAllClaims(token));
    }
}