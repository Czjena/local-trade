package io.github.czjena.local_trade.integration;

import io.github.czjena.local_trade.dto.UpdateUserDto;
import io.github.czjena.local_trade.exceptions.UserNotFoundException;

import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.ChatMessageRepository;
import io.github.czjena.local_trade.repository.UsersRepository;

import io.github.czjena.local_trade.service.infrastructure.UsersService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import resources.AbstractIntegrationTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = "security.jwt.secret-key=test-secret")
public class UserRepositoryIntegrationTest extends AbstractIntegrationTest {


    private final UsersRepository usersRepository;
    private final UsersService usersService;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    public UserRepositoryIntegrationTest (UsersRepository usersRepository, UsersService usersService) {
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }
    @Transactional
    @Test
    void whenSavingUser_thenUserSavedCorrectly() {
        chatMessageRepository.deleteAll();
        usersRepository.deleteAll();



        long countBefore = usersRepository.count();
        assertEquals(0, countBefore, "No users should be present before the test");

        Users user = new Users();
        user.setEmail("test@test.com");
        user.setPassword("test");
        user.setName("test");

        Users savedUser = usersRepository.save(user);

        assertNotNull(savedUser, "Saved user should be not null");
        assertTrue(savedUser.getId() > 0, "Id should be greater than 0"); // We check if the id was generated
        assertEquals("test", savedUser.getName(), "User name should be test");
        assertEquals("test", savedUser.getPassword(), "User password should be test");
    }
    @Test
    @Transactional
    void whenUpdatingUser_thenUserUpdatedCorrectly() {
        Users user = new Users();

        user.setEmail("test@test.com");
        user.setPassword("test");
        user.setName("test");
        Users savedUser = usersRepository.save(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateUserDto updateUserDto = new UpdateUserDto();

        updateUserDto.setEmail("test123@test.com");
        updateUserDto.setPassword("test123");
        updateUserDto.setName("test123");
        usersService.updateCurrentUser(updateUserDto);

        Users updatedUser = usersRepository.findById(savedUser.getId()).get();

        assertEquals("test123", updatedUser.getName(), "User name should be updated");
        assertNotEquals("test", updatedUser.getPassword(), "User password should be updated");
        assertEquals("test123@test.com", updatedUser.getEmail(), "User email should be updated");

    }

    @Test
    @Transactional
    void whenUpdatingUser_thenUserUpdatedCorrectly_whenUserNotFound() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail("test@test.com");
        updateUserDto.setPassword("test");
        updateUserDto.setName("test");

        Authentication auth = new UsernamePasswordAuthenticationToken(null, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> usersService.updateCurrentUser(updateUserDto));

    }

}

