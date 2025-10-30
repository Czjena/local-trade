package io.github.czjena.local_trade.integration;

import io.github.czjena.local_trade.controller.AuthenticationController;
import io.github.czjena.local_trade.dto.LoginDto;
import io.github.czjena.local_trade.dto.RefreshTokenRequest;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.RefreshTokenRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.response.LoginResponse;
import io.github.czjena.local_trade.service.JwtService;
import io.github.czjena.local_trade.service.TestJwtUtils;
import io.jsonwebtoken.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import resources.AbstractIntegrationTest;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@TestPropertySource(properties = "security.jwt.secret-key=41c6701ad7f5abf1db2b053a2f1a39ad41189e00462ec987622b5409dbc0006d")
@Testcontainers
public class JwtTokenTests extends AbstractIntegrationTest {

    @Autowired
    JwtService jwtService;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AuthenticationController authenticationController;
    @Autowired
    private javax.sql.DataSource dataSource;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void checkDataSource() throws SQLException {
        System.out.println("URL: " + dataSource.getConnection().getMetaData().getURL());
    }


    @Test
    public void jwtCreateUser_thenJwtIsCorrect() {
    Users user = new Users();
    user.setName("test");
    user.setEmail("test@test.com");
    user.setPassword("password");
    user.setRole("ROLE_USER");

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


        String expiredToken = TestJwtUtils.expiredToken(jwtService, user);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, user));
    }

    @Test
    public void jwtTokenForOtherUser_thenJwtIsNotValid() {
        Users user = new Users();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole("ROLE_USER");


        Users userB  = new Users();
        userB.setName("testB");
        userB.setEmail("testB@test.com");
        userB.setPassword("password");
        user.setRole("ROLE_USER");

        String token = jwtService.generateToken(user);

        assertFalse(jwtService.isTokenValid(token, userB));
    }
    @Test
    public void jwtTokenIsBroken() {
        Users user = new Users();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole("ROLE_USER");

        String token = jwtService.generateToken(user);
        String brokenToken = token +"abc";
        assertThrows(SignatureException.class, () -> jwtService.isTokenValid(brokenToken, user));
    }

    @Test
    @Transactional
    public void jwtTokenIsExpired_thenJwtIsRefreshed() {

        Users user = new Users();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole("ROLE_USER");
        System.out.println(user.getEmail());
        System.out.println(user.getPassword());

        usersRepository.save(user);

        LoginDto dto = new LoginDto();
        dto.setEmail("test@test.com");
        dto.setPassword("password");
        System.out.println(dto.getEmail());
        System.out.println(dto.getPassword());
        String expiredToken = TestJwtUtils.generateTokenWithCustomExpiration(jwtService, user);

        LoginResponse loginResponse = authenticationController.authenticate(dto);
        String refreshToken = loginResponse.getRefreshToken();
        RefreshTokenRequest requestToken = new RefreshTokenRequest(refreshToken);

        LoginResponse refreshTokenResponse = authenticationController.refreshToken(requestToken);

        String refreshedToken = refreshTokenResponse.getToken();


        assertTrue(jwtService.isTokenValid(refreshedToken, user));
        assertNotEquals(expiredToken, refreshedToken);

    }
    @Test
    @Transactional
    public void userLogOut_thenJwtIsNotValid() {
        Users user = new Users();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole("ROLE_USER");
        System.out.println(user.getEmail());
        System.out.println(user.getPassword());

        usersRepository.save(user);

        LoginDto dto = new LoginDto();
        dto.setEmail("test@test.com");
        dto.setPassword("password");
        System.out.println(dto.getEmail());
        System.out.println(dto.getPassword());

        LoginResponse loginResponse = authenticationController.authenticate(dto);
        String refreshToken = loginResponse.getRefreshToken();
        RefreshTokenRequest requestToken = new RefreshTokenRequest(refreshToken);

        authenticationController.logOut(requestToken);
        assertFalse(refreshTokenRepository.findByToken(refreshToken).isPresent());
    }
    @Test
    @Transactional
    public void getValidRoleFromToken_thenRolesAreValid() {
        Users user = new Users();
        user.setRole("ROLE_ADMIN");
        usersRepository.save(user);
        String token = jwtService.generateToken(user);
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) jwtService.extractClaim(token, claims -> claims.get("roles"));
        assertEquals(List.of("ROLE_ADMIN"), roles);


    }

    }
