package io.github.adrian.wieczorek.local_trade.mappers;

import io.github.adrian.wieczorek.local_trade.dto.LoginDto;
import io.github.adrian.wieczorek.local_trade.dto.UserResponseDto;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;

public class UserMapper {
    public static UserResponseDto toDto(UsersEntity user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setPassword(user.getPassword());
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());
        return userResponseDto;
    }
    private static LoginDto mapToUser(UsersEntity user) {
        LoginDto dto = new LoginDto();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return dto;
    }
}
