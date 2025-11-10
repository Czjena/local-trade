package io.github.czjena.local_trade.service.facade;

import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.mappers.AdvertisementDtoMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Image;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.request.RequestAdvertisementDto;
import io.github.czjena.local_trade.response.ResponseAdvertisementDto;
import io.github.czjena.local_trade.response.SimpleAdvertisementResponseDto;
import io.github.czjena.local_trade.service.infrastructure.AdvertisementService;
import io.github.czjena.local_trade.service.infrastructure.NotificationEventPublisher;
import io.github.czjena.local_trade.service.infrastructure.S3Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class NewAdvertisementFacade {
    private final AdvertisementService advertisementService;
    private final S3Service s3Service;
    private final UsersRepository usersRepository;
    private final AdvertisementDtoMapper advertisementDtoMapper;
    private final AdvertisementRepository advertisementRepository;

    public NewAdvertisementFacade(AdvertisementService advertisementService,
                                  S3Service s3Service,
                                  UsersRepository usersRepository,
                                  AdvertisementDtoMapper advertisementDtoMapper,
                                  AdvertisementRepository advertisementRepository
                                  ) {
        this.advertisementService = advertisementService;
        this.s3Service = s3Service;
        this.usersRepository = usersRepository;
        this.advertisementDtoMapper = advertisementDtoMapper;
        this.advertisementRepository = advertisementRepository;
    }

    @Transactional
    public ResponseAdvertisementDto addWholeAdvertisement(RequestAdvertisementDto advertisementDto, List<MultipartFile> images, UserDetails userDetails) throws IOException {
        Users user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        SimpleAdvertisementResponseDto advertDto = advertisementService.addAd(advertisementDto,user);
        Advertisement advertisement = advertisementRepository.findByAdvertisementId(advertDto.advertisementId())
                .orElseThrow(() -> new EntityNotFoundException("Advertisement not found"));
        if (images != null && !images.isEmpty()) {
            for (MultipartFile imageFile : images) {
                Image image = s3Service.uploadFile(advertisement.getAdvertisementId(), imageFile);
                advertisement.getImages().add(image);
            }
        }
        return advertisementDtoMapper.toResponseAdvertisementDto(advertisement);
 }
}
