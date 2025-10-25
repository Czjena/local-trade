package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.model.RefreshToken;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.RefreshTokenRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final UsersRepository usersRepository;


    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UsersRepository usersRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usersRepository = usersRepository;

    }

    public RefreshToken createRefreshToken(String name) {
        Users user = usersRepository.findByName(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        RefreshToken refreshToken = RefreshToken.builder()
                .users(user)
                .token(UUID.randomUUID().toString())
                .expires(Instant.now().plusMillis(600000))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiry(RefreshToken token) {
        if(token.getExpires().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Token is expired please sign in again");
        }
        return token;
    }

    public void revokeRefreshToken(String token) {
       refreshTokenRepository.deleteByToken(token);
    }
}

