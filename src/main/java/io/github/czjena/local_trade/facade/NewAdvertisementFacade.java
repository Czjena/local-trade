package io.github.czjena.local_trade.facade;

import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.mappers.AdvertisementDtoMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Image;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.request.RequestAdvertisementDto;
import io.github.czjena.local_trade.response.ResponseAdvertisementDto;
import io.github.czjena.local_trade.service.AdvertisementService;
import io.github.czjena.local_trade.service.S3Service;
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

    public NewAdvertisementFacade(AdvertisementService advertisementService, S3Service s3Service, UsersRepository usersRepository, AdvertisementDtoMapper advertisementDtoMapper) {
        this.advertisementService = advertisementService;
        this.s3Service = s3Service;
        this.usersRepository = usersRepository;
        this.advertisementDtoMapper = advertisementDtoMapper;
    }
    @Transactional
    public ResponseAdvertisementDto addWholeAdvertisement(RequestAdvertisementDto advertisementDto, List<MultipartFile> images, UserDetails userDetails) throws IOException {
        Users user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Advertisement ad = advertisementService.addAd(advertisementDto,user);
        if (images != null && !images.isEmpty()) {
            for (MultipartFile imageFile : images) {
                Image image = s3Service.uploadFile(ad.getAdvertisementId(), imageFile);
                ad.getImages().add(image);
            }
        }

        return advertisementDtoMapper.toResponseAdvertisementDto(ad);
 }
}
