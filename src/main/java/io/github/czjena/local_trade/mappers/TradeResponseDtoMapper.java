package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.request.TradeInitiationRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TradeInitiationRequestDtoMapper {

    @Mapping(source = "advertisement.advertisementId", target = "advertisementId")
    TradeInitiationRequestDto toTradeInitiationRequestDto(Trade trade);
}
