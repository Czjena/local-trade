package io.github.czjena.local_trade.dto;

import java.util.UUID;

public record ImageDto(
        UUID imageId,
        String url,
        long size,
        String contentType
) {}
