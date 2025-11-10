package io.github.czjena.local_trade.service.facade;
import io.github.czjena.local_trade.dto.LoginDto;
import io.github.czjena.local_trade.model.RefreshToken;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.response.LoginResponse;
import io.github.czjena.local_trade.service.infrastructure.AuthenticationService;
import io.github.czjena.local_trade.service.business.JwtService;
import io.github.czjena.local_trade.service.infrastructure.RefreshTokenService;
import org.springframework.stereotype.Service;

@Service
public class LoginFacade {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public LoginFacade(AuthenticationService authenticationService, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponse authenticateAndAssignNewRefreshToken(LoginDto loginDto){
        Users authenticatedUser  = authenticationService.authenticate(loginDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser);
         return LoginResponse.builder()
                .refreshToken(refreshToken.getToken())
                .token(jwtToken)
                .expiresIn(jwtService.getJwtExpiration())
                .build();
    }
}
