package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.model.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

public class TestJwtUtils {

    public static String expiredToken(JwtService jwtService, Users user) {
        Date expiredDate = new Date(System.currentTimeMillis() - 10_000); // 10s temu

        return Jwts
                .builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 20_000))
                .setExpiration(expiredDate)
                .signWith(jwtService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateTokenWithCustomExpiration(JwtService jwtService,Users user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())  // <- NOWY czas wydania
                .setExpiration(new Date(System.currentTimeMillis() + - 20_000))
                .signWith(jwtService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateToken(JwtService jwtService,Users user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 20_000))
                .signWith(jwtService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
