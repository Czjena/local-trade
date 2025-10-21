package io.github.czjena.local_trade.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.request.RequestAdvertisementDto;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.CategoryUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import resources.AbstractIntegrationTest;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "security.jwt.secret-key=41c6701ad7f5abf1db2b053a2f1a39ad41189e00462ec987622b5409dbc0006d")
@Testcontainers
@AutoConfigureMockMvc
public class NewAdvertisementFacadeTests extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @WithMockUser("test@test.com")
    @Test
    @Transactional
    public void testCreateWholeNewAdvertisement_thenReturnAdvertisementCreated() throws Exception {
        RequestAdvertisementDto baseAdvertisementDto = AdUtils.createRequestAdvertisementDto();
        List<MockMultipartFile> files = new ArrayList<>();
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.saveAndFlush(category);

        Users user = UserUtils.createUserRoleUser();
        usersRepository.saveAndFlush(user);

        RequestAdvertisementDto finalAdvertisementDto = baseAdvertisementDto.withCategoryId(category.getId());



        String advertisementJson = objectMapper.writeValueAsString(finalAdvertisementDto);

        MockMultipartFile jsonPart = new MockMultipartFile("advertisementDto","",MediaType.APPLICATION_JSON_VALUE,advertisementJson.getBytes());

        String listParameterName = "files";

        BufferedImage originalImage = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(originalImage,"jpg",os);
        byte[] imageBytes = os.toByteArray();

        for (int i = 0; i < 5; i++) {
            MockMultipartFile file = new MockMultipartFile(listParameterName, "file" + i + ".jpg",MediaType.IMAGE_JPEG_VALUE,imageBytes);
            files.add(file);
        }

        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/advertisements/new");

        requestBuilder.file(jsonPart);

        for (MockMultipartFile file : files) {
            requestBuilder.file(file);
        }

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrls").isArray())
                .andExpect(jsonPath("$.imageUrls",hasSize(5)))
                .andExpect(jsonPath("$.imageUrls[0]",isA(String.class)))
                .andExpect(jsonPath("$.imageUrls[0]",startsWith("http")))
                .andExpect(jsonPath("$.thumbnailUrls").isArray())
                .andExpect(jsonPath("$.thumbnailUrls", hasSize(5)))
                .andExpect(jsonPath("$.thumbnailUrls[0]", isA(String.class)))
                .andExpect(jsonPath("$.thumbnailUrls[0]", startsWith("http")));

    }
}
