package io.github.czjena.local_trade.integration;

import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.testutils.UserUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.testcontainers.junit.jupiter.Testcontainers;
import resources.AbstractIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "security.jwt.secret-key=41c6701ad7f5abf1db2b053a2f1a39ad41189e00462ec987622b5409dbc0006d")
@Testcontainers
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableWebSecurity
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AuthorizationTests extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UsersRepository usersRepository;

    @BeforeAll
    public void setup() {
        Users roleAdmin = UserUtils.createUserRoleAdmin();
        usersRepository.save(roleAdmin);
        Users roleUser = UserUtils.createUserRoleUser();
        usersRepository.save(roleUser);
    }
    @AfterAll
    public void cleanup() {
        usersRepository.deleteAll();
    }


    @Test
    @WithUserDetails(value = "testadmin@test.com", userDetailsServiceBeanName = "userDetailsService")
    public void testRoleAdmin_thenReturnOk() throws Exception {
        mockMvc.perform(get("/api/hello1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "test@test.com", userDetailsServiceBeanName = "userDetailsService")
    public void testRoleAdminWithRoleUserLoggedIn_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/hello1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "test@test.com", userDetailsServiceBeanName = "userDetailsService")
    public void testRoleUserWithRoleUserLoggedIn_thenReturnOk() throws Exception {
        mockMvc.perform(get("/api/hello2"))
                .andExpect(status().isOk());

    }

    @Test
    @WithUserDetails(value = "testadmin@test.com", userDetailsServiceBeanName = "userDetailsService")
    public void testRoleUserWithRoleAdminLoggedIn_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/hello2"))
                .andExpect(status().isForbidden());
    }
}
