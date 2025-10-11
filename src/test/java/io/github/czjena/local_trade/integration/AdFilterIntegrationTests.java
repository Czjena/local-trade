package io.github.czjena.local_trade.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.enums.SortDirection;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import resources.AbstractIntegrationTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        mockMvc.perform(
                        get("/advertisements/search")
                                .with(csrf())
                                .param("categoryId", String.valueOf(categoryId))
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(advertisements.size()))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
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


        mockMvc.perform(
                        get("/advertisements/search")
                                .with(csrf())
                                .param("title", title)
                                .param("page", "0")
                                .param("size", "10"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(advertisements.size()))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
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


        mockMvc.perform(
                        get("/advertisements/search")
                                .with(csrf())
                                .param("page","0")
                                .param("size", "10"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(advertisements.size()))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @Transactional
    public void filterByCategoryIdAndFilterForTitlePageAdvertisements_thenReturnPage() throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);

        List<Advertisement> advertisements = advertisementRepository.saveAll(
                IntStream.range(0, 10)
                        .mapToObj(i -> AdUtils.createAdvertisementRoleUserForIntegrationTests(category, user))
                        .toList()
        );


        mockMvc.perform(
                        get("/advertisements/search")
                                .with(csrf())
                                .param("categoryId", String.valueOf(advertisements.get(0).getCategory().getId()))
                                .param("title", String.valueOf(advertisements.get(0).getTitle()))
                                .param("page", "0")
                                .param("size", "10"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(advertisements.size()))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @Transactional
    public void filterByCategoryIdAndFilterForTitlePageAdvertisements_when_PriceIsBeyondAdverts_thenReturnPageWithNoMatches() throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);

        List<Advertisement> advertisements = advertisementRepository.saveAll(
                IntStream.range(0, 10)
                        .mapToObj(i -> AdUtils.createAdvertisementRoleUserForIntegrationTests(category, user))
                        .toList()
        );

        mockMvc.perform(
                        get("/advertisements/search")
                                .with(csrf())
                                .param("categoryId", String.valueOf(advertisements.get(0).getCategory().getId()))
                                .param("title", String.valueOf(advertisements.get(0).getTitle()))
                                .param("minPrice", "9999999")
                                .param("page", "0")
                                .param("size", "0"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @Transactional
    public void filterByCategoryIdAndTitleSortDirectionASCAndSortByPrice_thenReturnPage() throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);

        List<Advertisement> advertisements = advertisementRepository.saveAll(
                IntStream.range(0, 10)
                        .mapToObj(i -> AdFiltersUtils.createAdvertisementWithIndex(category,user,i))
                        .toList()
        );

        MvcResult result = mockMvc.perform(
                get("/advertisements/search")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "price,asc"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);

        List<BigDecimal> prices = new ArrayList<>();
        root.get("content").forEach(node -> prices.add(node.get("price").decimalValue()));

        List<BigDecimal> sorted = new ArrayList<>(prices);
        sorted.sort(Comparator.naturalOrder());

        assertThat(prices).isEqualTo(sorted);
    }
    @Test
    @Transactional
    public void filterByCategoryIdAndTitleSortDirectionDESCAndSortByTitle_thenReturnPage() throws Exception {
        Users user = UserUtils.createUserRoleUser();
        usersRepository.save(user);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);

        List<Advertisement> advertisements = advertisementRepository.saveAll(
                IntStream.range(0, 10)
                        .mapToObj(i -> AdFiltersUtils.createAdvertisementWithIndex(category,user,i))
                        .toList()
        );

        MvcResult result = mockMvc.perform(
                        get("/advertisements/search")
                                .with(csrf())
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "title,desc"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);

        List<String> titles = new ArrayList<>();
        root.get("content").forEach(node -> titles.add(node.get("title").asText()));

        List<String> sorted = new ArrayList<>(titles);
        sorted.sort(Comparator.reverseOrder());

        assertThat(titles).isEqualTo(sorted);
    }
}