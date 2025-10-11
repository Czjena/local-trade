package io.github.czjena.local_trade.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FavoriteAdvertisementDto(
        UUID advertisementId,
        String title,
        BigDecimal price,
        String MainImage
) {
}
