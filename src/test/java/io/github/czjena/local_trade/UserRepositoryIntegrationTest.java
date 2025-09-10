package io.github.czjena.local_trade;

import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import resources.AbstractIntegrationTest;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@SpringBootTest
public class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    UsersRepository usersRepository;

    @Test
    void whenSavingUser_thenUserSavedCorrectly() {

        long countBefore = usersRepository.count();
        assertEquals(0, countBefore, "No users should be present before the test");

        Users user = new Users();
        user.setEmail("test@test.com");
        user.setPassword("test");
        user.setName("test");

        Users savedUser = usersRepository.save(user);

        assertNotNull(savedUser, "Saved user should be not null");
        assertTrue(savedUser.getId() > 0, "Id should be greater than 0"); // We check if the Id was generated
        assertEquals("test", savedUser.getName(), "User name should be test");
        assertEquals("test", savedUser.getPassword(), "User password should be test");
    }
}
