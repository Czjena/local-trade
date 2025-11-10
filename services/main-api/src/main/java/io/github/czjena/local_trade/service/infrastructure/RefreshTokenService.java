package io.github.czjena.local_trade.service.infrastructure;

import io.github.czjena.local_trade.dto.RefreshTokenRequest;
import io.github.czjena.local_trade.model.RefreshToken;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.response.LoginResponse;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenService {
    @Transactional
    RefreshToken createRefreshToken(Users user);
    @Transactional(readOnly = true)
    LoginResponse generateNewTokenFromRefresh(RefreshTokenRequest refreshTokenRequest);
    @Transactional
    RefreshToken verifyExpiry(RefreshToken token);
    @Transactional
    void revokeRefreshToken(String token);
}
