package io.github.czjena.local_trade.response;

import java.util.UUID;

public record ReviewResponseDto(
        int rating,
        String comment,
        UUID reviewId
) {
}
