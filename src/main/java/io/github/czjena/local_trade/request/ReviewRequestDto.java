package io.github.czjena.local_trade.request;

public record ReviewRequestDto(
        int rating,
        String comment
) {
}
