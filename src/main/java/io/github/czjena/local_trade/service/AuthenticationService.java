package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.LoginDto;
import io.github.czjena.local_trade.dto.RegisterUsersDto;
import io.github.czjena.local_trade.model.Users;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AuthenticationService {
    @Transactional
    Users signup(RegisterUsersDto dto);
    @Transactional
    Users authenticate(LoginDto dto);

    List<String> getAuthenticatedRoles();
}
