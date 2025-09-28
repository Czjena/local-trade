package io.github.czjena.local_trade.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.testutils.UserUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import resources.AbstractIntegrationTest;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "security.jwt.secret-key=41c6701ad7f5abf1db2b053a2f1a39ad41189e00462ec987622b5409dbc0006d")
@Testcontainers
@AutoConfigureMockMvc
public class AdvTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    @WithMockUser(username = "test@test.com")
    @Transactional
    public void whenUserHasRoleUser_thenAdIsAdded() throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);

        Category category = Category.builder()
                .name("Car")
                .description("Car")
                .parentCategory("Vehicle")
                .build();
        categoryRepository.save(category);

        AdvertisementDto ad = new AdvertisementDto(
                category.getId(),
                new BigDecimal("149.99"),
                "Audi A4 B6",
                "audi_a4.jpg",
                "Well maintained, 1.9 TDI",
                true,
                "Warsaw"
        );

        String adJson = objectMapper.writeValueAsString(ad);

        mockMvc.perform(post("/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Audi A4 B6"))
                .andExpect(jsonPath("$.category.name").value("Car"));


    }
}
