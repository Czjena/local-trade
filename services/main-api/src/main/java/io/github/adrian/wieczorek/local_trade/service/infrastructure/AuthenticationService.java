package io.github.adrian.wieczorek.local_trade.service.infrastructure;

import io.github.adrian.wieczorek.local_trade.dto.LoginDto;
import io.github.adrian.wieczorek.local_trade.dto.RegisterUsersDto;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AuthenticationService {
    @Transactional
    UsersEntity signup(RegisterUsersDto dto);
    @Transactional
    UsersEntity authenticate(LoginDto dto);

    List<String> getAuthenticatedRoles();
}
