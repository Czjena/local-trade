package io.github.czjena.local_trade.response;

import java.util.UUID;

public record SimpleAdvertisementResponseDto(
        UUID advertisementId,
        String title
){}
