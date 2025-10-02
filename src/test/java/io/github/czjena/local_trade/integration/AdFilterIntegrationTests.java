package io.github.czjena.local_trade.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.testutils.AdFiltersUtils;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.CategoryUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import resources.AbstractIntegrationTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "security.jwt.secret-key=41c6701ad7f5abf1db2b053a2f1a39ad41189e00462ec987622b5409dbc0006d")
@Testcontainers
@AutoConfigureMockMvc
public class AdFilterIntegrationTests extends AbstractIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void filterByCategoryIdAndPageAdvertisements_thenReturnPageOfAdvertisements() throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);

        List<Advertisement> advertisements = advertisementRepository.saveAll(
                IntStream.range(0, 10)
                        .mapToObj(i -> AdUtils.createAdvertisementRoleUserForIntegrationTests(category, user))
                        .toList()
        );
        Integer categoryId = category.getId();
        Pageable pageable = PageRequest.of(0, 10);
        AdvertisementFilterDto advertisementFilterDto = AdFiltersUtils.filterByCategory(categoryId);
        String advertisementFilterDtoJson = objectMapper.writeValueAsString(advertisementFilterDto);

        mockMvc.perform(
                post("/filter")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(advertisementFilterDtoJson)
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size",String.valueOf(pageable.getPageSize())))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(advertisements.size()))
                .andExpect(jsonPath("$.number").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.size").value(pageable.getPageSize()));
    }
    @Test
    @Transactional
    public void filterByTitleAndPageAdvertisements_thenReturnPageOfAdvertisements() throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);

        List<Advertisement> advertisements = advertisementRepository.saveAll(
                IntStream.range(0, 10)
                        .mapToObj(i -> AdUtils.createAdvertisementRoleUserForIntegrationTests(category, user))
                        .toList()
        );
        String title = advertisements.get(0).getTitle();
        Pageable pageable = PageRequest.of(0, 10);
        AdvertisementFilterDto advertisementFilterDto = AdFiltersUtils.filterByTitle(title);
        String advertisementFilterDtoJson = objectMapper.writeValueAsString(advertisementFilterDto);

        mockMvc.perform(
                        post("/filter")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(advertisementFilterDtoJson)
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size",String.valueOf(pageable.getPageSize())))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(advertisements.size()))
                .andExpect(jsonPath("$.number").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.size").value(pageable.getPageSize()));
    }
    @Test
    @Transactional
    public void filterAndPageAdvertisements_thenReturnPageOfAdvertisements() throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);

        List<Advertisement> advertisements = advertisementRepository.saveAll(
                IntStream.range(0, 10)
                        .mapToObj(i -> AdUtils.createAdvertisementRoleUserForIntegrationTests(category, user))
                        .toList()
        );

        Pageable pageable = PageRequest.of(0, 10);
        AdvertisementFilterDto advertisementFilterDto = AdFiltersUtils.getAdvertisementFilterDto();
        String advertisementFilterDtoJson = objectMapper.writeValueAsString(advertisementFilterDto);

        mockMvc.perform(
                        post("/filter")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(advertisementFilterDtoJson)
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size",String.valueOf(pageable.getPageSize())))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.number").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.size").value(pageable.getPageSize()));
    }
    @Test
    @Transactional
    public void filterByCategoryIdAndFilterForTitlePageAdvertisements_thenReturnPage() throws Exception   {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);

        List<Advertisement> advertisements = advertisementRepository.saveAll(
                IntStream.range(0, 10)
                        .mapToObj(i -> AdUtils.createAdvertisementRoleUserForIntegrationTests(category, user))
                        .toList()
        );
        Pageable pageable = PageRequest.of(0, 10);
        AdvertisementFilterDto advertisementFilterDto = AdFiltersUtils.filterByTitleAndCategoryAndMaxPrice(advertisements.get(0).getTitle(),advertisements.get(0).getPrice(),category.getId());
        String advertisementFilterDtoJson = objectMapper.writeValueAsString(advertisementFilterDto);

        mockMvc.perform(
                        post("/filter")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(advertisementFilterDtoJson)
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size",String.valueOf(pageable.getPageSize())))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.number").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.size").value(pageable.getPageSize()));
    }
    @Test
    @Transactional
    public void filterByCategoryIdAndFilterForTitlePageAdvertisements_when_PriceIsBeyondAdverts_thenReturnPageWithNoMatches() throws Exception   {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);

        List<Advertisement> advertisements = advertisementRepository.saveAll(
                IntStream.range(0, 10)
                        .mapToObj(i -> AdUtils.createAdvertisementRoleUserForIntegrationTests(category, user))
                        .toList()
        );
        Pageable pageable = PageRequest.of(0, 10);
        AdvertisementFilterDto advertisementFilterDto = AdFiltersUtils.filterByTitleAndCategoryAndMaxPrice(advertisements.get(0).getTitle(), new BigDecimal("1"), category.getId());
        String advertisementFilterDtoJson = objectMapper.writeValueAsString(advertisementFilterDto);

        mockMvc.perform(
                        post("/filter")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(advertisementFilterDtoJson)
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size",String.valueOf(pageable.getPageSize())))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.number").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.size").value(pageable.getPageSize()));
    }



}
