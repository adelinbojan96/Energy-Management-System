package com.example.demo.security;

import com.example.demo.entities.Credential;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "mysecretkey12345678901234567890123456789012";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    public String generateToken(Credential credential) {
        return Jwts.builder()
                .setSubject(credential.getUsername())
                .claim("credentialId", credential.getId().toString())
                .claim("role", credential.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }
}
