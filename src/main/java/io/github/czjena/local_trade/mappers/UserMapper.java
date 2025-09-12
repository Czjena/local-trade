package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.dto.UserResponseDto;
import io.github.czjena.local_trade.model.Users;

public class UserMapper {
    public static UserResponseDto toDto(Users user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setPassword(user.getPassword());
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());
        return userResponseDto;
    }
}
