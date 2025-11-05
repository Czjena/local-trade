package io.github.czjena.local_trade.dto;

import lombok.Data;

import java.math.BigDecimal;

public record AdvertisementUpdateDto(
    BigDecimal price,
    String title,
    String description,
    String location,
    String image
){}
