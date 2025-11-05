package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.dto.FavoriteAdvertisementDto;
import io.github.czjena.local_trade.model.Advertisement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteAdvertisementMapper {

    @Mapping(source = "advertisementId", target = "advertisementId")
    FavoriteAdvertisementDto toDto(Advertisement advertisement);
}