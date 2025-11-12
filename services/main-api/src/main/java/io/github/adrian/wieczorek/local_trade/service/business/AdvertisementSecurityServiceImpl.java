package io.github.adrian.wieczorek.local_trade.service.business;

import io.github.adrian.wieczorek.local_trade.model.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.repository.AdvertisementRepository;
import io.github.adrian.wieczorek.local_trade.service.infrastructure.AdvertisementSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdvertisementSecurityServiceImpl implements AdvertisementSecurityService {
    private final AdvertisementRepository advertisementRepository;


    @Override
    public boolean isOwner(Authentication authentication, Integer advertisement) {
        String username = authentication.getName();
        Optional<AdvertisementEntity> ad = advertisementRepository.findById(advertisement);
        return ad.map(value -> value.getUser().getUsername().equals(username)).orElse(false);
    }
    @Override
    public boolean isOwner(UserDetails userDetails, UUID advertisementId) {
        String username = userDetails.getUsername();
        Optional<AdvertisementEntity> ad =  advertisementRepository.findByAdvertisementId(advertisementId);
        return ad.map(value -> value.getUser().getUsername().equals(username)).orElse(false);
    }
}
