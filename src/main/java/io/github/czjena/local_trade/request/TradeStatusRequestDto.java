package io.github.czjena.local_trade.request;

import io.github.czjena.local_trade.enums.TradeStatus;
import jakarta.validation.constraints.NotNull;

public record TradeStatusRequestDto(
        @NotNull
        TradeStatus tradeStatus
) {
}
