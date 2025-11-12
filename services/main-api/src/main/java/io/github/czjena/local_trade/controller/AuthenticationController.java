package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.dto.LoginDto;
import io.github.czjena.local_trade.dto.RefreshTokenRequest;
import io.github.czjena.local_trade.dto.RegisterUsersDto;
import io.github.czjena.local_trade.service.facade.LoginFacade;

import io.github.czjena.local_trade.response.LoginResponse;
import io.github.czjena.local_trade.service.infrastructure.AuthenticationService;
import io.github.czjena.local_trade.service.infrastructure.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final LoginFacade loginFacade;


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