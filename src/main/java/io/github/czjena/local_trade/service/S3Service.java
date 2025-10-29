package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.ImageDto;
import io.github.czjena.local_trade.model.Image;
import jakarta.annotation.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public interface S3Service {
    @Transactional
    PutObjectRequest putObject(String bucketName, String key, @Nullable String content);
    @Transactional
    Image uploadFile(UUID advertisementId, MultipartFile file) throws IOException;
    @Transactional
    void deleteFile(UUID imageId);
    @Transactional
    List<ImageDto> listFiles(UUID advertisementId);
    byte[] generateThumbnail(MultipartFile file) throws IOException;
    @Transactional
    String generatePresignedUrl(String key, Duration duration);
}
