package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.dto.UpdateUserDto;
import io.github.czjena.local_trade.dto.UserResponseDto;
import io.github.czjena.local_trade.mappers.UserMapper;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsersController(UsersService usersService, UsersRepository usersRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/me")
    public ResponseEntity<Users> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUsers = (Users) authentication.getPrincipal();
        return ResponseEntity.ok(currentUsers);
    }

    @GetMapping("/")
    public ResponseEntity<List<Users>> allUsers() {
        List<Users> users = usersService.allUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateCurrentUser(@RequestBody UpdateUserDto updateUserDto) {
        UserResponseDto updatedUser = usersService.updateCurrentUser(updateUserDto);
        return ResponseEntity.ok(updatedUser);

    }


}