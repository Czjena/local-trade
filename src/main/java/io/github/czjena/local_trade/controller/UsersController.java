package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
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
}