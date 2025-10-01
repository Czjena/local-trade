package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.mappers.AdvertisementMapperToAdvertisementDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementFilterService {
    private final AdvertisementRepository advertisementRepository;
    public AdvertisementFilterService(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    public Page<AdvertisementDto> filterAndPageAdvertisements(AdvertisementFilterDto advertisementFilterDto,Pageable pageable) {
        Page<Advertisement> advertisements = advertisementRepository.findAll(advertisementFilterDto,pageable);
        return advertisements.map(AdvertisementMapperToAdvertisementDto::toDto);
    }
}
