package io.github.adrian.wieczorek.local_trade.service.facade;
import io.github.adrian.wieczorek.local_trade.dto.LoginDto;
import io.github.adrian.wieczorek.local_trade.model.RefreshTokenEntity;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import io.github.adrian.wieczorek.local_trade.response.LoginResponse;
import io.github.adrian.wieczorek.local_trade.service.infrastructure.AuthenticationService;
import io.github.adrian.wieczorek.local_trade.service.business.JwtService;
import io.github.adrian.wieczorek.local_trade.service.infrastructure.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginFacade {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;



    public LoginResponse authenticateAndAssignNewRefreshToken(LoginDto loginDto){
        UsersEntity authenticatedUser  = authenticationService.authenticate(loginDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.createRefreshToken(authenticatedUser);
         return LoginResponse.builder()
                .refreshToken(refreshTokenEntity.getToken())
                .token(jwtToken)
                .expiresIn(jwtService.getJwtExpiration())
                .build();
    }
}
