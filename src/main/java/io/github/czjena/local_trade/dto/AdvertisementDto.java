package io.github.czjena.local_trade.dto;

import java.math.BigDecimal;

public record AdvertisementDto(
        Integer categoryId,
        BigDecimal price,
        String title,
        String image,
        String description,
        boolean active,
        String location
) {}