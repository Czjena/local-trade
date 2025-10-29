package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.response.ResponseAdvertisementDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

public interface AdvertisementFilterService {
    Specification<Advertisement> getSpecification(AdvertisementFilterDto filter);
    @Transactional
    Page<ResponseAdvertisementDto> filterAndPageAdvertisements(AdvertisementFilterDto advertisementFilterDto, Pageable pageable);
}
