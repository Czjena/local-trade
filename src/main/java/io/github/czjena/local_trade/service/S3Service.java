package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.ImageDto;
import io.github.czjena.local_trade.mappers.ImageMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Image;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.ImageRepository;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final AdvertisementRepository advertisementRepository;
    private final String bucketName = "advertisements";
    private final ImageRepository imageRepository;
    private final S3Presigner s3Presigner;


    public S3Service(S3Client s3Client, AdvertisementRepository advertisementRepository, ImageRepository imageRepository, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.advertisementRepository =  advertisementRepository;
        this.imageRepository = imageRepository;
        this.s3Presigner = s3Presigner;
    }

    public Image uploadFile(UUID advertisementId, MultipartFile file) throws IOException {
        Advertisement ad = advertisementRepository.findByAdvertisementId(advertisementId);
        Image image = new Image();
        image.setAdvertisement(ad);
        String fileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        String key = ad.getId() + "/full/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        image.setKey(key);
        image.setUrl(generatePresignedUrl(key, Duration.ofHours(1)));
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        imageRepository.save(image);

        return image;
    }
    public void deleteFile(UUID imageId){
        Image image = imageRepository.findByImageId(imageId);
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(image.getKey()).build());
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(image.getThumbnailKey()).build());
    }
    public List<Image> listFiles (UUID advertisementId){
        Advertisement ad = advertisementRepository.findByAdvertisementId(advertisementId);
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(ad.getId() + "/full/")
                .build();

        return s3Client.listObjectsV2(request).contents().stream().map(s3Object ->  {
            Image image = new Image();
            image.setKey(s3Object.key());
            image.setAdvertisement(ad);
            image.setSize(s3Object.size());
            image.setUrl(generatePresignedUrl(s3Object.key()+ "/full/", Duration.ofMinutes(10)));
            return image;
        }).toList();

    }


    private byte[] generateThumbnail(MultipartFile file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        BufferedImage thumb = Scalr.resize(bufferedImage, 150);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(thumb, "jpg", os);
        return os.toByteArray();
    }

    public String generatePresignedUrl(String key, Duration duration) {
        S3Presigner presigner = s3Presigner;
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .getObjectRequest(GetObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build())
                        .signatureDuration(duration)
                        .build()
        );
        presigner.close();
        return presignedRequest.url().toString();
    }

}
