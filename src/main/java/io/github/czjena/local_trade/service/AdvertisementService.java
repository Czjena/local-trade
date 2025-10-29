package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.AdvertisementUpdateDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.request.RequestAdvertisementDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public interface AdvertisementService {
    @Transactional
    Advertisement addAd(RequestAdvertisementDto dto, UserDetails userDetails);
    @Transactional
    Advertisement getAdvertisementById(Integer advertisementId);
    @Transactional
    AdvertisementUpdateDto changeAdvertisement(AdvertisementUpdateDto dto, UserDetails userDetails, Integer advertisementId);
    @Transactional
    void deleteAdvertisement(UserDetails userDetails, Integer advertisementId);
}
