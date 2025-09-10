package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.model.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${JWT_SECRET}")
    private String secretString;

    byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
    SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

    public String generateToken(Users user) {
        long expirationMs = 3600000;
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }
}
