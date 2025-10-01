package io.github.czjena.local_trade.dto;


import java.math.BigDecimal;


public record AdvertisementFilterDto(
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String location,
        String title,
        String description,
        Boolean active,
        Integer page,
        Integer size,
        String sortBy,
        String sortDirection
) {}

