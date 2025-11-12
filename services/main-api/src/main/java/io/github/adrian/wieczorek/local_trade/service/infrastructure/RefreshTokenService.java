package io.github.adrian.wieczorek.local_trade.service.infrastructure;

import io.github.adrian.wieczorek.local_trade.dto.RefreshTokenRequest;
import io.github.adrian.wieczorek.local_trade.model.RefreshTokenEntity;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import io.github.adrian.wieczorek.local_trade.response.LoginResponse;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenService {
    @Transactional
    RefreshTokenEntity createRefreshToken(UsersEntity user);
    @Transactional(readOnly = true)
    LoginResponse generateNewTokenFromRefresh(RefreshTokenRequest refreshTokenRequest);
    @Transactional
    RefreshTokenEntity verifyExpiry(RefreshTokenEntity token);
    @Transactional
    void revokeRefreshToken(String token);
}
