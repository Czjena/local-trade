package io.github.czjena.local_trade.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.czjena.local_trade.dto.CategoryDto;
import io.github.czjena.local_trade.dto.ImageDto;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.testutils.AdUtilsIntegrationTests;
import io.github.czjena.local_trade.testutils.CategoryUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonParser;
import resources.AbstractIntegrationTest;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "security.jwt.secret-key=41c6701ad7f5abf1db2b053a2f1a39ad41189e00462ec987622b5409dbc0006d")
@Testcontainers
@AutoConfigureMockMvc
@EnableWebSecurity
public class CategoryIntegrationTests extends AbstractIntegrationTest {
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@test.com")
    @Transactional
    public void getAllCategories_thenCategoriesAreReturned() throws Exception {
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value("test"));

    }
    @Test
    @WithMockUser(username = "testadmin@test.com", roles = "ADMIN")
    @Transactional
    public void postCategory_thenCategoriesAreReturned() throws Exception {
        Users user = UserUtils.createUserRoleAdmin();
        usersRepository.save(user);
        CategoryDto categoryDto = CategoryUtils.createCategoryDto();

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test category"))
                .andExpect(jsonPath("$.description").value("Category for testing"))
                .andExpect(jsonPath("$.parentCategory").value("Test parent category"));


    }

    @ParameterizedTest
    @CsvSource({
            "POST, /categories , 201",
            "PUT , /categories/{id}, 200",
            "DELETE, /categories/{id}, 204 "
    })
    @WithMockUser(username = "testadmin@test.com", roles = "ADMIN")
    @Transactional
    public void happyPathForPostingDeleteAndChangeCategory_thenCategoriesAreReturned(String httpMethod, String endpointTemplate, int expectedStatus) throws Exception {
        Users user = UserUtils.createUserRoleAdmin();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);
        CategoryDto categoryDto = CategoryUtils.createCategoryDto();

        String finalEndpoint = endpointTemplate.replace("{id}", category.getId().toString());
        ResultActions resultActions;

        String jsonDtoToCategory = objectMapper.writeValueAsString(categoryDto);

        switch (httpMethod.toUpperCase()) {
            case "POST":
                resultActions = mockMvc.perform(post(finalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDtoToCategory))
                        .andExpect(jsonPath("$.name").value("Test category"))
                        .andExpect(jsonPath("$.description").value("Category for testing"))
                        .andExpect(jsonPath("$.parentCategory").value("Test parent category"));
                break;

            case "PUT":
                resultActions = mockMvc.perform(put(finalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDtoToCategory))
                        .andExpect(jsonPath("$.name").value("Test category"))
                        .andExpect(jsonPath("$.description").value("Category for testing"))
                        .andExpect(jsonPath("$.parentCategory").value("Test parent category"));
                break;

            case "DELETE":
                resultActions = mockMvc.perform(delete(finalEndpoint));
                break;
            default:
                throw new Exception("Unsupported HTTP method: " + httpMethod);
        }
                      resultActions.andExpect(status().is(expectedStatus));
        }


    @ParameterizedTest
    @CsvSource({
            "POST, /categories , 403",
            "PUT , /categories/{id}, 403",
            "DELETE, /categories/{id}, 403 "
    })
    @WithMockUser(username = "test@test.com", roles = "USER")
    @Transactional
    public void badPathForPostingDeleteAndChangeCategoryWithUnauthorizedUser_thenCategoriesAreNotReturned(String httpMethod, String endpointTemplate, int expectedStatus) throws Exception{
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        CategoryDto categoryDto = CategoryUtils.createCategoryDto();
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);
        String finalEndpoint = endpointTemplate.replace("{id}",category.getId().toString());
        ResultActions resultActions;
        String jsonDtoToCategory = objectMapper.writeValueAsString(categoryDto);

        switch (httpMethod.toUpperCase()) {
            case "POST":
                resultActions = mockMvc.perform(post(finalEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDtoToCategory));
                break;
                case "PUT":
                    resultActions = mockMvc.perform(put(finalEndpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonDtoToCategory));
                    break;
                    case "DELETE":
                    resultActions = mockMvc.perform(delete(finalEndpoint));
                    break;
                    default:
                        throw new Exception("Unsupported HTTP method: " + httpMethod);
        }
        resultActions.andExpect(status().is(expectedStatus));
    }
    @ParameterizedTest
    @CsvSource({
            "POST, /categories , 400",
            "PUT , /categories/, 400",
            "DELETE, /categories/, 404 "
    })
    @WithMockUser(username = "test@test.com", roles = "ADMIN")
    @Transactional
    public void badPathWrongRequest_thenReturnBadRequest(String httpMethod, String endpointTemplate, int expectedStatus) throws Exception{
        ImageDto dto = new ImageDto(UUID.randomUUID(),"999","999",9999,"9999");
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);

        ResultActions resultActions;
        String jsonDtoToCategory = objectMapper.writeValueAsString(dto);

        switch (httpMethod.toUpperCase()) {
            case "POST":
                resultActions = mockMvc.perform(post(endpointTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDtoToCategory));
                break;
            case "PUT":
                resultActions = mockMvc.perform(put(endpointTemplate+ category.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDtoToCategory));
                break;
            case "DELETE":
                resultActions = mockMvc.perform(delete(endpointTemplate + "43443"));
                break;
            default:
                throw new Exception("Unsupported HTTP method: " + httpMethod);
        }
        resultActions.andExpect(status().is(expectedStatus));
    }
    }



