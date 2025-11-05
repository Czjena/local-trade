package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.dto.LoginDto;
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
    private static LoginDto mapToUser(Users user) {
        LoginDto dto = new LoginDto();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return dto;
    }
}
