package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.UpdateUserDto;
import io.github.czjena.local_trade.dto.UserResponseDto;
import io.github.czjena.local_trade.model.Users;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UsersService {
    @Transactional(readOnly = true)
    List<Users> allUsers();
    @Transactional
    UserResponseDto updateCurrentUser(UpdateUserDto dto);
}
