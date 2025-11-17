package io.github.adrian.wieczorek.local_trade.controller;

import io.github.adrian.wieczorek.local_trade.service.user.dto.UpdateUserDto;
import io.github.adrian.wieczorek.local_trade.service.user.dto.UserResponseDto;
import io.github.adrian.wieczorek.local_trade.service.user.UsersEntity;
import io.github.adrian.wieczorek.local_trade.service.user.service.UsersFinder;
import io.github.adrian.wieczorek.local_trade.service.user.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersFinder usersFinder;
    private final UsersService usersService;


    @GetMapping("/me")
    public ResponseEntity<UsersEntity> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsersEntity currentUsersEntity = (UsersEntity) authentication.getPrincipal();
        return ResponseEntity.ok(currentUsersEntity);
    }

    @GetMapping("/")
    public ResponseEntity<List<UsersEntity>> allUsers() {
        List<UsersEntity> users = usersFinder.allUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateCurrentUser(@RequestBody UpdateUserDto updateUserDto, @AuthenticationPrincipal UserDetails currentUser) {
        String email = currentUser.getUsername();
        UserResponseDto updatedUser = usersService.updateCurrentUser(updateUserDto,email);
        return ResponseEntity.ok(updatedUser);
    }
}