package io.github.adrian.wieczorek.local_trade.service.infrastructure;

import io.github.adrian.wieczorek.local_trade.dto.UpdateUserDto;
import io.github.adrian.wieczorek.local_trade.dto.UserResponseDto;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UsersService {
    @Transactional(readOnly = true)
    List<UsersEntity> allUsers();
    @Transactional
    UserResponseDto updateCurrentUser(UpdateUserDto dto);
}
