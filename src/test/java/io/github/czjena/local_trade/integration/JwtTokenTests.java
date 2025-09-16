package io.github.czjena.local_trade.integration;

import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.JwtService;
import io.github.czjena.local_trade.service.TestJwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import resources.AbstractIntegrationTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "security.jwt.secret-key=41c6701ad7f5abf1db2b053a2f1a39ad41189e00462ec987622b5409dbc0006d")
public class JwtTokenTests extends AbstractIntegrationTest {

    @Autowired
    JwtService jwtService;
    @Autowired
    private UsersRepository usersRepository;


    @Test
    public void jwtCreateUser_thenJwtIsCorrect() {
    Users user = new Users();
    user.setName("test");
    user.setEmail("test@test.com");
    user.setPassword("password");
    usersRepository.save(user);
    String token = jwtService.generateToken(user);
    String email = jwtService.extractClaim(token, Claims::getSubject);
    assertTrue(jwtService.isTokenValid(token, user));
    assertEquals("test@test.com", email);
    Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
    Date now = new Date();
    assertTrue(expiration.after(now), "Token powinien być ważny");
    }

    @Test
    public void jwtExpiredToken_thenJwtIsNotValid() {
        Users user = new Users();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setPassword("password");
        usersRepository.save(user);

        String expiredToken = TestJwtUtils.expiredToken(jwtService, user);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, user));
    }

    @Test
    public void jwtTokenForOtherUser_thenJwtIsNotValid() {
        Users user = new Users();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setPassword("password");
        usersRepository.save(user);

        Users userB  = new Users();
        userB.setName("testB");
        userB.setEmail("testB@test.com");
        userB.setPassword("password");

        String token = jwtService.generateToken(user);

        assertFalse(jwtService.isTokenValid(token, userB));
    }
    @Test
    public void jwtTokenIsBroken() {
        Users user = new Users();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setPassword("password");
        usersRepository.save(user);
        String token = jwtService.generateToken(user);
        String brokenToken = token +"abc";
        assertThrows(SignatureException.class, () -> jwtService.isTokenValid(brokenToken, user));
    }
    }
