package io.github.czjena.local_trade.response;

import io.github.czjena.local_trade.enums.TradeStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeResponseDto(
        Long id,
        TradeStatus status,
        BigDecimal proposedPrice,
        LocalDateTime createdAt,
        boolean buyerMarkedCompleted,
        boolean sellerMarkedCompleted,
        SimpleUserResponseDto buyerSimpleDto,
        SimpleUserResponseDto sellerSimpleDto,
        SimpleAdvertisementResponseDto simpleAdvertisementResponseDto
) {}
