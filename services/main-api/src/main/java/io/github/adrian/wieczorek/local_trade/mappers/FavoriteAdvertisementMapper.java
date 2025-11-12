package io.github.adrian.wieczorek.local_trade.mappers;

import io.github.adrian.wieczorek.local_trade.dto.FavoriteAdvertisementDto;
import io.github.adrian.wieczorek.local_trade.model.AdvertisementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteAdvertisementMapper {

    @Mapping(source = "advertisementId", target = "advertisementId")
    FavoriteAdvertisementDto toDto(AdvertisementEntity advertisementEntity);
}