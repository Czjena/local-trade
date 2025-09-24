package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.model.Advertisement;

import java.math.BigDecimal;

public class AdvertisementMapper {
    public static Advertisement toEntity(Advertisement dto) {
        Advertisement ad = new Advertisement();
        ad.setId(dto.getId());
        ad.setDescription(dto.getDescription());
        ad.setTitle(dto.getTitle());
        ad.setPrice(dto.getPrice());
        ad.setCategory(dto.getCategory());
        ad.setLocation(dto.getLocation());
        return ad;

    }
}
