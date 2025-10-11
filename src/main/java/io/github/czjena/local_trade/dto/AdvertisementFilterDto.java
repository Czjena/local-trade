package io.github.czjena.local_trade.dto;


import java.math.BigDecimal;


public record AdvertisementFilterDto(
        Integer categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String location,
        String title,
        Boolean active
) {}


