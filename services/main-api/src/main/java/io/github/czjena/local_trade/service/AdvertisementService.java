package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.AdvertisementUpdateDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.request.RequestAdvertisementDto;
import io.github.czjena.local_trade.response.ResponseAdvertisementDto;
import io.github.czjena.local_trade.response.SimpleAdvertisementResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface AdvertisementService {
    @Transactional
    SimpleAdvertisementResponseDto addAd(RequestAdvertisementDto dto, UserDetails userDetails);
    @Transactional(readOnly = true)
    ResponseAdvertisementDto getAdvertisementById(UUID advertisementId);
    @Transactional
    AdvertisementUpdateDto changeAdvertisement(AdvertisementUpdateDto dto, UserDetails userDetails, Integer advertisementId);
    @Transactional
    void deleteAdvertisement(UserDetails userDetails, Integer advertisementId);
}
