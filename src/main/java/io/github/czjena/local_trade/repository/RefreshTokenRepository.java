package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.RefreshToken;
import io.github.czjena.local_trade.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

}
