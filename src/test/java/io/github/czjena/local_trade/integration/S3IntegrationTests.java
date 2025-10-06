package io.github.czjena.local_trade.integration;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.AdvertisementService;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.CategoryUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import resources.AbstractIntegrationTest;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "security.jwt.secret-key=41c6701ad7f5abf1db2b053a2f1a39ad41189e00462ec987622b5409dbc0006d")
@Testcontainers
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "s3.useMinio=true",
        "s3.endpoint=http://localhost:9000"
})
public class S3IntegrationTests extends AbstractIntegrationTest {

    @Autowired
    S3Client s3Client;
    private static final String bucketName = "advertisements";
    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Transactional
    @Test
    void uploadAndGetFile() throws IOException {
        // upload pliku
        String key = "test-image.jpg";
        byte[] content = "dummy content".getBytes(StandardCharsets.UTF_8);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                RequestBody.fromBytes(content)
        );

        // sprawdzamy, czy plik istnieje
        ListObjectsResponse objects = s3Client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build());
        boolean exists = objects.contents().stream().anyMatch(o -> o.key().equals(key));
        assertThat(exists).isTrue();

        // usuwamy plik
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());

        // sprawdzamy, że usunięto
        ListObjectsResponse afterDelete = s3Client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build());
        boolean stillExists = afterDelete.contents().stream().anyMatch(o -> o.key().equals(key));
        assertThat(stillExists).isFalse();
    }

    @Test
    @Transactional
    public void uploadFile_thenFileIsUploaded() throws Exception {
        Users user = usersRepository.save(UserUtils.createUserRoleUser());
        Category category = categoryRepository.save(CategoryUtils.createCategoryForIntegrationTests());
        Advertisement ad = AdUtils.createAdvertisementRoleUserForIntegrationTests(category, user);
        advertisementRepository.save(ad);


        MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test-image.jpg",
                    "image/jpeg",
                    "dummy content".getBytes(StandardCharsets.UTF_8)
            );

            mockMvc.perform(
                            multipart("/image/" + ad.getAdvertisementId())
                                    .file(file)
                                    .with(csrf())
                    )
                    .andExpect(status().isCreated());

        }
    }

