package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.dto.LoginDto;
import io.github.czjena.local_trade.dto.RefreshTokenRequest;
import io.github.czjena.local_trade.dto.RegisterUsersDto;
import io.github.czjena.local_trade.model.RefreshToken;
import io.github.czjena.local_trade.model.Users;

import io.github.czjena.local_trade.repository.RefreshTokenRepository;
import io.github.czjena.local_trade.response.LoginResponse;
import io.github.czjena.local_trade.service.AuthenticationService;
import io.github.czjena.local_trade.service.JwtService;
import io.github.czjena.local_trade.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    private final RefreshTokenService refreshTokenService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Users> register(@RequestBody RegisterUsersDto registerUserDto) {
        Users registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public LoginResponse authenticate(@RequestBody LoginDto loginUserDto) {
        Users authenticatedUser = authenticationService.authenticate(loginUserDto);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser.getName());
        String jwtToken = jwtService.generateToken(authenticatedUser);


        return LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .refreshToken(refreshToken.getToken()).build();
    }
    @PostMapping("/refreshToken")
    @Operation(summary = "Refresh token for users when jwt token expires")
    public LoginResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
      return refreshTokenService.findByToken((refreshTokenRequest.getToken()))
                .map(refreshTokenService::verifyExpiry)
                .map(RefreshToken::getUsers)
                .map(Users -> {
                   String accesstoken = jwtService.generateToken(Users);
                   return LoginResponse.builder()
                           .token(accesstoken)
                           .refreshToken((refreshTokenRequest.getToken()))
                           .build();

                }).orElseThrow(()-> new RuntimeException("Refresh token not found"));

    }
    @PostMapping("/logout")
    public ResponseEntity<?> logOut(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.revokeRefreshToken(refreshTokenRequest.getToken());
        return ResponseEntity.ok().build();
    }
}