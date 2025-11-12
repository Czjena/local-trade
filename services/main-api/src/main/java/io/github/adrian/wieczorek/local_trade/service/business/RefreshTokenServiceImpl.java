package io.github.adrian.wieczorek.local_trade.service.business;

import io.github.adrian.wieczorek.local_trade.dto.RefreshTokenRequest;
import io.github.adrian.wieczorek.local_trade.exceptions.UserNotFoundException;
import io.github.adrian.wieczorek.local_trade.model.RefreshTokenEntity;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import io.github.adrian.wieczorek.local_trade.repository.RefreshTokenRepository;
import io.github.adrian.wieczorek.local_trade.response.LoginResponse;
import io.github.adrian.wieczorek.local_trade.service.infrastructure.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;



    @Override
    @Transactional
    public RefreshTokenEntity createRefreshToken(UsersEntity user) {
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .usersEntity(user)
                .token(UUID.randomUUID().toString())
                .expires(Instant.now().plusMillis(600000))
                .build();
        return refreshTokenRepository.save(refreshTokenEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse generateNewTokenFromRefresh(RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenRepository.findByToken((refreshTokenRequest.getToken()))
                .map(this::verifyExpiry)
                .map(RefreshTokenEntity::getUsersEntity)
                .map(Users -> {
                    String accessToken = jwtService.generateToken(Users);
                    return LoginResponse.builder()
                            .token(accessToken)
                            .refreshToken((refreshTokenRequest.getToken()))
                            .build();
                }).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public RefreshTokenEntity verifyExpiry(RefreshTokenEntity token) {
        if(token.getExpires().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Token is expired please sign in again");
        }
        return token;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
       refreshTokenRepository.deleteByToken(token);
    }
}

