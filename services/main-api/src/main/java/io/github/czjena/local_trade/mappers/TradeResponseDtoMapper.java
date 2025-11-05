package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Trade;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.request.TradeInitiationRequestDto;
import io.github.czjena.local_trade.response.SimpleAdvertisementResponseDto;
import io.github.czjena.local_trade.response.SimpleUserResponseDto;
import io.github.czjena.local_trade.response.TradeResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TradeResponseDtoMapper {

    @Mapping(source = "buyer",target = "buyerSimpleDto")
    @Mapping(source = "seller",target = "sellerSimpleDto")
    @Mapping(source = "advertisement", target = "simpleAdvertisementResponseDto")
    TradeResponseDto tradeToTradeResponseDto(Trade trade);

    SimpleUserResponseDto toSimpleUserDto(Users users);
    SimpleAdvertisementResponseDto toSimpleAdvertisementResponseDto(Advertisement advertisement);
}
