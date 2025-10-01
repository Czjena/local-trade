package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.dto.AdvertisementUpdateDto;
import io.github.czjena.local_trade.model.Advertisement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class AdvertisementMapperToAdvertisementUpdateDto {
    public static AdvertisementUpdateDto toDto(Advertisement advertisement) {
        return new AdvertisementUpdateDto(advertisement.getPrice(), advertisement.getTitle(), advertisement.getDescription(), advertisement.getLocation(), advertisement.getImage());

    }

}
