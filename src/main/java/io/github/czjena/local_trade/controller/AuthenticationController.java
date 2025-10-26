package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.dto.LoginDto;
import io.github.czjena.local_trade.dto.RefreshTokenRequest;
import io.github.czjena.local_trade.dto.RegisterUsersDto;
import io.github.czjena.local_trade.facade.LoginFacade;
import io.github.czjena.local_trade.model.RefreshToken;
import io.github.czjena.local_trade.model.Users;

import io.github.czjena.local_trade.repository.RefreshTokenRepository;
import io.github.czjena.local_trade.response.LoginResponse;
import io.github.czjena.local_trade.service.AuthenticationService;
import io.github.czjena.local_trade.service.JwtService;
import io.github.czjena.local_trade.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final LoginFacade loginFacade;

    public AuthenticationController(AuthenticationService authenticationService, RefreshTokenService refreshTokenService, LoginFacade loginFacade) {
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.loginFacade = loginFacade;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterUsersDto registerUserDto) {
        authenticationService.signup(registerUserDto);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/login")
    public LoginResponse authenticate(@RequestBody @Valid LoginDto loginUserDto) {
        return loginFacade.authenticateAndAssignNewRefreshToken(loginUserDto);
    }

    @PostMapping("/refreshToken")
    @Operation(summary = "Refresh token for users when jwt token expires")
    public LoginResponse refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.generateNewTokenFromRefresh(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOut(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.revokeRefreshToken(refreshTokenRequest.getToken());
        return ResponseEntity.ok().build();
    }
}