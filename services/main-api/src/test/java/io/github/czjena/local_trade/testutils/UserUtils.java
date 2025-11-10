package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.dto.LoginDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Users;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.UUID;

@SpringBootTest
public class UserUtils {


    public static Users createUserRoleUser() {
        Users user = new Users();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole("ROLE_USER");
        user.setFavoritedAdvertisements(new HashSet<>());
        return user;
    }
    public static Users createUserRoleUserBuyer() {
        Users user = new Users();
        user.setName("buyer");
        user.setEmail("buyer@test.com");
        user.setPassword("password");
        user.setRole("ROLE_USER");
        user.setFavoritedAdvertisements(new HashSet<>());
        return user;
    }
    public static Users createUserRoleUserSeller() {
        Users user = new Users();
        user.setName("seller");
        user.setEmail("seller@test.com");
        user.setPassword("password");
        user.setRole("ROLE_USER");
        user.setFavoritedAdvertisements(new HashSet<>());
        return user;
    }
    public static Users createUserRoleUserUnitTestWithUUID() {
        Users user = new Users();
        user.setUserId(UUID.randomUUID());
        user.setName("buyer");
        user.setEmail("buyer@test.com");
        user.setPassword("password");
        user.setRole("ROLE_USER");
        user.setFavoritedAdvertisements(new HashSet<>());
        return user;
    }


    public static Users createUserRoleAdmin() {
        Users user = new Users();
        user.setName("test admin");
        user.setEmail("testadmin@test.com");
        user.setPassword("password");
        user.setRole("ROLE_ADMIN");
        return user;
    }
    public static LoginDto createLoginDto(Users user) {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(user.getEmail());
        loginDto.setPassword(user.getPassword());
        return loginDto;
    }
}
