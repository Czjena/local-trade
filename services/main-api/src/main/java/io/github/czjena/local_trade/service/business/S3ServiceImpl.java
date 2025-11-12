package io.github.czjena.local_trade.service.business;

import io.github.czjena.local_trade.dto.ImageDto;
import io.github.czjena.local_trade.mappers.ImageMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Image;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.ImageRepository;
import io.github.czjena.local_trade.service.infrastructure.S3Service;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final AdvertisementRepository advertisementRepository;
    private final String bucketName = "advertisements";
    private final ImageRepository imageRepository;
    private final S3Presigner s3Presigner;



    @Override
    public PutObjectRequest putObject(String bucketName, String key, @Nullable String content) {
        return  PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(content)
                .build();
    }

    @Override
    @Transactional
    public Image uploadFile(UUID advertisementId, MultipartFile file) throws IOException {
        Advertisement ad = advertisementRepository.findByAdvertisementId(advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));
        Image image = new Image();
        image.setAdvertisement(ad);
        String fileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        String key = ad.getId() + "/full/" + fileName;

        byte[] thumbnail = generateThumbnail(file);
        String thumbnailKey = ad.getId() + "/thumbnail/" + fileName;

       PutObjectRequest thumbnailRequest = putObject(bucketName,thumbnailKey,null);
       s3Client.putObject(thumbnailRequest, RequestBody.fromBytes(thumbnail));

       PutObjectRequest putObjectRequest = putObject(bucketName,key,file.getContentType());
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        image.setKey(key);
        image.setUrl(generatePresignedUrl(key, Duration.ofHours(1)));
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setThumbnailKey(thumbnailKey);
        image.setThumbnailUrl(generatePresignedUrl(thumbnailKey, Duration.ofHours(1)));
        image.setImageId(UUID.randomUUID());
        imageRepository.save(image);
        return image;
    }

    @Override
    @Transactional
    public void deleteFile(UUID imageId) {
        Image image = imageRepository.findByImageId(imageId);

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(image.getKey()).build());
        } catch (S3Exception e) {
            log.error("Delete failed/Image doesn't exist", e);
        }
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(image.getThumbnailKey()).build());
        } catch (S3Exception e) {
            log.error("Delete failed/Thumbnail doesn't exist", e);
        }
        imageRepository.delete(image);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageDto> listFiles(UUID advertisementId) {
        Advertisement ad = advertisementRepository.findByAdvertisementId(advertisementId)
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));
        List<Image> images = imageRepository.findAllByAdvertisement(ad);

        return images.stream().map(image -> {
            String thumbnailKey = image.getKey().replace("full/", "thumbnail/");
            image.setUrl(generatePresignedUrl(image.getKey(), Duration.ofHours(1)));
            image.setThumbnailUrl(generatePresignedUrl(thumbnailKey, Duration.ofHours(1)));
            return ImageMapper.ImagetoImageDto(image);
        }).toList();
    }


    @Override
    public byte[] generateThumbnail(MultipartFile file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        BufferedImage thumb = Scalr.resize(bufferedImage, 150);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(thumb, "jpg", os);
        return os.toByteArray();
    }

    @Override
    @Transactional
    public String generatePresignedUrl(String key, Duration duration) {
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .getObjectRequest(GetObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build())
                        .signatureDuration(duration)
                        .build()
        );
        return presignedRequest.url().toString();
    }
}
