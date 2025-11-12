package io.github.adrian.wieczorek.local_trade.mappers;

import io.github.adrian.wieczorek.local_trade.model.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.model.TradeEntity;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import io.github.adrian.wieczorek.local_trade.response.SimpleAdvertisementResponseDto;
import io.github.adrian.wieczorek.local_trade.response.SimpleUserResponseDto;
import io.github.adrian.wieczorek.local_trade.response.TradeResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TradeResponseDtoMapper {

    @Mapping(source = "buyer",target = "buyerSimpleDto")
    @Mapping(source = "seller",target = "sellerSimpleDto")
    @Mapping(source = "advertisementEntity", target = "simpleAdvertisementResponseDto")
    TradeResponseDto tradeToTradeResponseDto(TradeEntity tradeEntity);

    SimpleUserResponseDto toSimpleUserDto(UsersEntity usersEntity);
    SimpleAdvertisementResponseDto toSimpleAdvertisementResponseDto(AdvertisementEntity advertisementEntity);
}
