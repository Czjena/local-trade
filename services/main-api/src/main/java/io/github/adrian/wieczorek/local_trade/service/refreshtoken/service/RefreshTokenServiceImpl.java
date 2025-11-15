package io.github.adrian.wieczorek.local_trade.service.refreshtoken.service;

import io.github.adrian.wieczorek.local_trade.service.user.service.JwtService;
import io.github.adrian.wieczorek.local_trade.service.refreshtoken.dto.RefreshTokenRequest;
import io.github.adrian.wieczorek.local_trade.exceptions.UserNotFoundException;
import io.github.adrian.wieczorek.local_trade.service.refreshtoken.RefreshTokenEntity;
import io.github.adrian.wieczorek.local_trade.service.user.UsersEntity;
import io.github.adrian.wieczorek.local_trade.service.refreshtoken.RefreshTokenRepository;
import io.github.adrian.wieczorek.local_trade.service.user.dto.LoginResponse;
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

