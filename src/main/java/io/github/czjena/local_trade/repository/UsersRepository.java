package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Integer Id(int id);

    Optional<Users> findByEmail(String email);
}
