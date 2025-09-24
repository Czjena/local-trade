package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import org.springframework.stereotype.Service;


@Service
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    public AdvertisementService(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    public Advertisement addAd(AdvertisementDto dto, Users user) {
        Advertisement ad = Advertisement.builder()
                .category(dto.category())
                .price(dto.price())
                .title(dto.title())
                .image(dto.image())
                .description(dto.description())
                .active(dto.active())
                .location(dto.location())
                .user(user)
                .build();

        return advertisementRepository.save(ad);
    }
}
