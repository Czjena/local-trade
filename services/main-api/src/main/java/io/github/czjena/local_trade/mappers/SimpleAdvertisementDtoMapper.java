package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.response.SimpleAdvertisementResponseDto;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface SimpleAdvertisementDtoMapper {

    SimpleAdvertisementResponseDto advertisementToSimpleDto(Advertisement advertisement);

}
