package io.github.czjena.local_trade.dto;


import io.github.czjena.local_trade.enums.AdvertisementSortField;
import io.github.czjena.local_trade.enums.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;


public record AdvertisementFilterDto(
        Integer categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String location,
        String title,
        String description,
        Boolean active,
        @Schema(description = "Sortowanie po polu ", allowableValues = {"PRICE","TITLE","CREATED_AT}"})
        List<AdvertisementSortField> sortBy,
        @Schema(description = "Kierunek Sortowania", allowableValues = {"ASC","DESC"})
        SortDirection sortDirection
) {}


