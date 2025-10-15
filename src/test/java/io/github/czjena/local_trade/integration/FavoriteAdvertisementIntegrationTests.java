package io.github.czjena.local_trade.integration;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.FavoriteAdvertisementService;
import io.github.czjena.local_trade.testutils.AdUtilsIntegrationTests;
import io.github.czjena.local_trade.testutils.UserUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;
import resources.AbstractIntegrationTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "security.jwt.secret-key=41c6701ad7f5abf1db2b053a2f1a39ad41189e00462ec987622b5409dbc0006d")
@Testcontainers
@AutoConfigureMockMvc
public class FavoriteAdvertisementIntegrationTests extends AbstractIntegrationTest {
    @Autowired
    private FavoriteAdvertisementService favoriteAdvertisementService;
    @Autowired
    private AdUtilsIntegrationTests adUtilsIntegrationTests;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsersRepository usersRepository;

    @ParameterizedTest
    @CsvSource({
            "GET, /favorite/me, 200",
            "POST, /favorite/{id}, 204",
            "DELETE, /favorite/{id}, 204 "
    })
    @Transactional
    @WithMockUser("test@test.com")
    public void checkMyFavoriteAdvertisement_thenReturnFavoriteAdvertisement(String httpMethod ,String endpointTemplate, int expectedStatus) throws Exception {
        Advertisement ad = adUtilsIntegrationTests.createAdWithUserAndCategoryAutomaticRoleUser("test","test", BigDecimal.valueOf(300));
        Users user = ad.getUser();

        String finalEndpoint = endpointTemplate.replace("{id}", ad.getAdvertisementId().toString());
        ResultActions resultActions;

        switch(httpMethod.toUpperCase()) {
            case "POST":
                resultActions = mockMvc.perform(post(finalEndpoint));
                break;
            case "GET":
                resultActions = mockMvc.perform(get(finalEndpoint));
                break;
            case "DELETE":
                user.getFavoritedAdvertisements().add(ad);
                ad.getFavoritedByUsers().add(user);
                resultActions = mockMvc.perform(delete(finalEndpoint));
                break;
                default:
                    throw new Exception("Unsupported HTTP method: " + httpMethod);
        }
        resultActions.andExpect(status().is(expectedStatus));

    }
    @ParameterizedTest
    @CsvSource({
            "GET, /favorite/me, 403",
            "POST, /favorite/{id}, 403",
            "DELETE, /favorite/{id}, 403 "
    })
    @Transactional
    public void whenTryingToFavoriteAdvertisementsNotLoggedIn_returnForbidden(String httpMethod ,String endpointTemplate, int expectedStatus) throws Exception {
        Advertisement ad = adUtilsIntegrationTests.createAdWithUserAndCategoryAutomaticRoleUser("test","test", BigDecimal.valueOf(300));
        Users user = ad.getUser();

        String finalEndpoint = endpointTemplate.replace("{id}", ad.getAdvertisementId().toString());
        ResultActions resultActions;

        switch(httpMethod.toUpperCase()) {
            case "POST":
                resultActions = mockMvc.perform(post(finalEndpoint));
                break;
            case "GET":
                resultActions = mockMvc.perform(get(finalEndpoint));
                break;
            case "DELETE":
                user.getFavoritedAdvertisements().add(ad);
                ad.getFavoritedByUsers().add(user);
                resultActions = mockMvc.perform(delete(finalEndpoint));
                break;
            default:
                throw new Exception("Unsupported HTTP method: " + httpMethod);
        }
        resultActions.andExpect(status().is(expectedStatus));
    }

    @ParameterizedTest
    @CsvSource({
            "GET, /favorite/me, 200",
            "POST, /favorite/{id}, 404",
            "DELETE, /favorite/{id}, 404 "
    })
    @Transactional
    @WithMockUser("test@test.com")
    public void whenTryingToCheckFavoriteAdvertisements_thenAdvertIsNotPresent_returnNoAdvertisements(String httpMethod ,String endpointTemplate, int expectedStatus) throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        String finalEndpoint = endpointTemplate.replace("{id}", UUID.randomUUID().toString());
        ResultActions resultActions;

        switch(httpMethod.toUpperCase()) {
            case "POST":
                resultActions = mockMvc.perform(post(finalEndpoint));
                break;
            case "GET":
                resultActions = mockMvc.perform(get(finalEndpoint))
                        .andExpect(content().json("[]")); // Return 200 because we return empty set
                break;
            case "DELETE":
                resultActions = mockMvc.perform(delete(finalEndpoint));
                break;
            default:
                throw new Exception("Unsupported HTTP method: " + httpMethod);
        }
        resultActions.andExpect(status().is(expectedStatus));
    }

}
