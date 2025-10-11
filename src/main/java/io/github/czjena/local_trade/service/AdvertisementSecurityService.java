package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.mappers.AdvertisementMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdvertisementSecurityService {
    private AdvertisementRepository advertisementRepository;
    public AdvertisementSecurityService(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }
    public boolean isOwner(Authentication authentication, Integer advertisement) {
        String username = authentication.getName();
        Optional<Advertisement> ad = advertisementRepository.findById(advertisement);
        return ad.map(value -> value.getUser().getUsername().equals(username)).orElse(false);
    }
}
