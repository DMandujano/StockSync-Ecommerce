package com.stockSync.backend.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import static java.lang.System.currentTimeMillis;

@Service
public class JwtService {

    // 1. La anotación @Value va EXCLUSIVAMENTE sobre el atributo de texto.
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaim(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails, boolean rememberMe) {
        long expirationMs = rememberMe ? 1000L * 60 * 60 * 24 * 7 : 1000L * 60 * 60 * 24;
        return generateToken(new HashMap<>(), userDetails, expirationMs);
    }

    public String generateToken(Map<String, Object> extraclaims, UserDetails userDetails, long expirationMs) {
        return Jwts.builder()
                .claims(extraclaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaim(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ✅ CORREGIDO: Se eliminó la anotación @Value duplicada que estaba sobre este método.
    // Este es un método de soporte interno ordinario.
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}