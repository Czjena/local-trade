package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.model.Image;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.ImageRepository;
import io.github.czjena.local_trade.service.business.S3ServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3UnitTests {
    @Mock
    private AdvertisementRepository advertisementRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    S3Client s3Client;
    @InjectMocks
    S3ServiceImpl s3Service;

    @BeforeAll
    static void setupRegion() {
        System.setProperty("aws.region", "us-east-1");
    }
    @Test
    public void createThumbnail_thenThumbnailIsCreated() throws IOException {
        BufferedImage original = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(original,"jpg",os);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "flower.jpg",
                "jpeg",
                os.toByteArray()
        );
        byte[] thumbnail = s3Service.generateThumbnail(file);
        Assertions.assertNotNull(thumbnail);
        Assertions.assertNotEquals(file.getBytes().length, thumbnail.length);
        Assertions.assertTrue(thumbnail.length < file.getBytes().length);
    }

    @Test
    public void testForImageRepository_thenReturnImageGetKey() {
        Image image = new Image();
        image.setImageId(UUID.randomUUID());
        image.setKey("some/key.jpg");

        when(imageRepository.findByImageId(image.getImageId())).thenReturn(image);

        Image found = imageRepository.findByImageId(image.getImageId());

        verify(imageRepository, times(1)).findByImageId(image.getImageId());
        Assertions.assertNotNull(found);
        assertEquals("some/key.jpg", found.getKey());
    }
}