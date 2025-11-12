package io.github.adrian.wieczorek.local_trade.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public record TradeInitiationRequestDto(
        BigDecimal proposedPrice,
        @NotNull
        UUID advertisementId
) {
}
