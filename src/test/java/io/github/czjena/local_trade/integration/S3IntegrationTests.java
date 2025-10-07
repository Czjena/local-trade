package io.github.czjena.local_trade.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.czjena.local_trade.dto.ImageDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Image;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.repository.ImageRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.AdUtilsIntegrationTests;
import io.github.czjena.local_trade.testutils.CategoryUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;
import resources.AbstractIntegrationTest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private AdvertisementRepository advertisementRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AdUtilsIntegrationTests adUtilsIntegrationTests;

    @Transactional
    @Test
    void uploadAndGetFile() throws IOException {
        String key = "test-image.jpg";
        byte[] content = "dummy content".getBytes(StandardCharsets.UTF_8);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                RequestBody.fromBytes(content)
        );

        ListObjectsResponse objects = s3Client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build());
        boolean exists = objects.contents().stream().anyMatch(o -> o.key().equals(key));
        assertThat(exists).isTrue();

        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());

        ListObjectsResponse afterDelete = s3Client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build());
        boolean stillExists = afterDelete.contents().stream().anyMatch(o -> o.key().equals(key));
        assertThat(stillExists).isFalse();
    }

    @BeforeEach
    public void cleanBucket() {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        s3Client.listObjectsV2(listRequest)
                .contents()
                .forEach(obj -> s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(obj.key())
                        .build()));
    }


    @Test
    @Transactional
    public void uploadFile_whenFileIsUploadedAndCreated_thenDeleteFile() throws Exception {
       Advertisement ad = adUtilsIntegrationTests.createAdWithUserAndCategoryAutomaticRoleUser("title","mock description", BigDecimal.valueOf(333));
        BufferedImage original = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(original, "jpg", os);


        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                os.toByteArray()
        );

        mockMvc.perform(
                        multipart("/image/" + ad.getAdvertisementId())
                                .file(file)
                                .with(csrf())
                )
                .andExpect(status().isCreated());


        MvcResult result = mockMvc.perform(get("/image")
                .param("advertisementId",ad.getAdvertisementId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        List<ImageDto> images = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertThat(images).hasSize(1);
        Assertions.assertNotNull(images.get(0).url());
        Assertions.assertNotNull(images.get(0).imageId());

       Image image = imageRepository.findByAdvertisement(ad);

       Assertions.assertEquals(images.get(0).imageId(), image.getImageId());

        mockMvc.perform(delete("/image/{id}",image.getImageId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/image")
                .param("advertisementId",ad.getAdvertisementId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

    }
}

