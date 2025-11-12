package io.github.adrian.wieczorek.local_trade.controller;

import io.github.adrian.wieczorek.local_trade.dto.UpdateUserDto;
import io.github.adrian.wieczorek.local_trade.dto.UserResponseDto;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import io.github.adrian.wieczorek.local_trade.service.infrastructure.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;


    @GetMapping("/me")
    public ResponseEntity<UsersEntity> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsersEntity currentUsersEntity = (UsersEntity) authentication.getPrincipal();
        return ResponseEntity.ok(currentUsersEntity);
    }

    @GetMapping("/")
    public ResponseEntity<List<UsersEntity>> allUsers() {
        List<UsersEntity> users = usersService.allUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateCurrentUser(@RequestBody UpdateUserDto updateUserDto) {
        UserResponseDto updatedUser = usersService.updateCurrentUser(updateUserDto);
        return ResponseEntity.ok(updatedUser);
    }
}