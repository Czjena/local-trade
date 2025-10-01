package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.dto.LoginDto;
import io.github.czjena.local_trade.model.Users;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserUtils {


    public static Users createUserRoleUser() {
        Users user = new Users();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole("ROLE_USER");
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
    public static LoginDto createLoginDto() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(createLoginDto().getEmail());
        loginDto.setPassword(createLoginDto().getPassword());
        return loginDto;
    }
}
