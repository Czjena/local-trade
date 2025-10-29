package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.RefreshTokenRequest;
import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.model.RefreshToken;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.RefreshTokenRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.response.LoginResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final UsersRepository usersRepository;
    private final JwtService jwtService;


    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UsersRepository usersRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Users user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .users(user)
                .token(UUID.randomUUID().toString())
                .expires(Instant.now().plusMillis(600000))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public LoginResponse generateNewTokenFromRefresh(RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenRepository.findByToken((refreshTokenRequest.getToken()))
                .map(this::verifyExpiry)
                .map(RefreshToken::getUsers)
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
    public RefreshToken verifyExpiry(RefreshToken token) {
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

