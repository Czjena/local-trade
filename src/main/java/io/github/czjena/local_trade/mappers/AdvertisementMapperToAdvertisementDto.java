package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.model.Advertisement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class AdvertisementMapperToAdvertisementDto {
    public static AdvertisementDto toDto(Advertisement ad) {
        return new AdvertisementDto(
                ad.getCategory() != null ? ad.getCategory().getId() : null,
                ad.getPrice(),
                ad.getTitle(),
                ad.getImage(),
                ad.getDescription(),
                ad.isActive(),
                ad.getLocation()
        );
    }
}
