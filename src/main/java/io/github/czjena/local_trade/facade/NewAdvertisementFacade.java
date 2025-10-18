package io.github.czjena.local_trade.facade;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.AdvertisementService;
import io.github.czjena.local_trade.service.S3Service;
import io.github.czjena.local_trade.service.UsersService;
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
    private final UsersService usersService;
    private final UsersRepository usersRepository;

    public NewAdvertisementFacade(AdvertisementService advertisementService, S3Service s3Service, UsersService usersService, UsersRepository usersRepository) {
        this.advertisementService = advertisementService;
        this.s3Service = s3Service;
        this.usersService = usersService;
        this.usersRepository = usersRepository;
    }
    @Transactional
    public Advertisement addWholeAdvertisement(AdvertisementDto advertisementDto, List<MultipartFile> images, UserDetails userDetails) throws IOException {
        Users user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Advertisement ad = advertisementService.addAd(advertisementDto,user);
        if (images != null && !images.isEmpty()) {
            for (MultipartFile imageFile : images) {
                s3Service.uploadFile(ad.getAdvertisementId(), imageFile);
            }
        }
        return ad;
 }
}
