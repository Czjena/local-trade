package io.github.czjena.local_trade.request;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public record TradeInitiationRequestDto(
        BigDecimal proposedPrice,
        UUID advertisementId
) {
}
