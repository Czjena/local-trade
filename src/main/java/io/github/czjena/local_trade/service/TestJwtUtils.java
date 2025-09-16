package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.model.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

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
}
