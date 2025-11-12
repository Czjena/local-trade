package io.github.adrian.wieczorek.local_trade.dto;

import java.util.UUID;

public record ImageDto(
        UUID imageId,
        String url,
        String thumbnailUrl,
        long size,
        String contentType
) {}
