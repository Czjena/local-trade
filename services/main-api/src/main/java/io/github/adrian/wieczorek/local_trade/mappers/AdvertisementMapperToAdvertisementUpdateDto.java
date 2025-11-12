package io.github.adrian.wieczorek.local_trade.mappers;

import io.github.adrian.wieczorek.local_trade.dto.AdvertisementUpdateDto;
import io.github.adrian.wieczorek.local_trade.model.AdvertisementEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class AdvertisementMapperToAdvertisementUpdateDto {
    public static AdvertisementUpdateDto toDto(AdvertisementEntity advertisementEntity) {
        return new AdvertisementUpdateDto(advertisementEntity.getPrice(), advertisementEntity.getTitle(), advertisementEntity.getDescription(), advertisementEntity.getLocation(), advertisementEntity.getImage());

    }

}
