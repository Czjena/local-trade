package io.github.czjena.local_trade.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.czjena.local_trade.dto.AdvertisementUpdateDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.request.RequestAdvertisementDto;
import io.github.czjena.local_trade.response.ResponseAdvertisementDto;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.CategoryUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @Autowired
    private AdvertisementRepository advertisementRepository;


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

        RequestAdvertisementDto ad = new RequestAdvertisementDto(
                category.getId(),
                new BigDecimal("149.99"),
                "Audi A4 B6",
                "audi_a4.jpg",
                "Well maintained, 1.9 TDI",
                true,
                "Warsaw"

        );


        String adJson = objectMapper.writeValueAsString(ad);

        mockMvc.perform(post("/advertisements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Audi A4 B6"));

    }
    @Test
    @Transactional
    @WithMockUser(username = "test@test.com")
    public void postAdvertisementId_thenAdvertisementIsReturned() throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);
        Advertisement ad = AdUtils.createAdvertisementRoleUserForIntegrationTests(category,user);
        advertisementRepository.save(ad);

        mockMvc.perform(get("/advertisements/get/" + ad.getAdvertisementId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title test"))
                .andExpect(jsonPath("$.price").value(ad.getPrice()))
                .andExpect(jsonPath("$.description").value(ad.getDescription()));
    }

    @Test
    @Transactional
    @WithMockUser(username = "test@test.com")
    public void updateAdvertisementEndpoint_thenAdvertisementIsUpdated() throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);
        Advertisement ad = AdUtils.createAdvertisementRoleUserForIntegrationTests(category,user);
        advertisementRepository.save(ad);
        AdvertisementUpdateDto updatedDto = new AdvertisementUpdateDto(ad.getPrice(), "changedtext", "changedtext", ad.getImage(), ad.getImage());
        String updatedDtoJson = objectMapper.writeValueAsString(updatedDto);

        mockMvc.perform(put("/advertisements/update/" + ad.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("changedtext"))
                .andExpect(jsonPath("$.description").value("changedtext"));
    }
    @Test
    @Transactional
    @WithMockUser(username = "testadmin@test.com")
    public void updateAdvertisementWithBadData_thenAdvertisementIsNotUpdated() throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Users user2 = UserUtils.createUserRoleAdmin();
        usersRepository.save(user2);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);
        Advertisement ad = AdUtils.createAdvertisementRoleUserForIntegrationTests(category,user);
        advertisementRepository.save(ad);
        AdvertisementUpdateDto updatedDto = new AdvertisementUpdateDto(ad.getPrice(), "changedtext", "changedtext", ad.getImage(), ad.getImage());
        String updatedDtoJson = objectMapper.writeValueAsString(updatedDto);

        mockMvc.perform(put("/advertisements/update/" + ad.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedDtoJson))
                .andExpect(status().isNotFound());
    }
    @Test
    @Transactional
    public void tryToUpdateButNotLoggedIn_thenUnauthorized() throws Exception {
        mockMvc.perform(put("/advertisements/update/1"))
                .andExpect(status().isForbidden());
    }

    }
