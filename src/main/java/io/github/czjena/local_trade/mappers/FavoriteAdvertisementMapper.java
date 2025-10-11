package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.dto.FavoriteAdvertisementDto;
import io.github.czjena.local_trade.model.Advertisement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FavoriteAdvertisementMapper {

    @Mapping(source = "advertisementId", target = "advertisementId")
    FavoriteAdvertisementDto toDto(Advertisement advertisement);
}