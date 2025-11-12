package io.github.adrian.wieczorek.local_trade.mappers;

import io.github.adrian.wieczorek.local_trade.model.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.response.SimpleAdvertisementResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SimpleAdvertisementDtoMapper {

    SimpleAdvertisementResponseDto advertisementToSimpleDto(AdvertisementEntity advertisementEntity);

}
