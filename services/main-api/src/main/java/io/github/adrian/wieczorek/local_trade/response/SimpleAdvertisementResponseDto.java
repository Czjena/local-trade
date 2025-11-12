package io.github.adrian.wieczorek.local_trade.response;

import java.util.UUID;

public record SimpleAdvertisementResponseDto(
        UUID advertisementId,
        String title
){}
