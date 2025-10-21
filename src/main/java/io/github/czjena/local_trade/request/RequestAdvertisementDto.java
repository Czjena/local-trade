package io.github.czjena.local_trade.request;

import java.math.BigDecimal;

    public record RequestAdvertisementDto(
        Integer categoryId,
        BigDecimal price,
        String title,
        String image,
        String description,
        boolean active,
        String location
) {
        public RequestAdvertisementDto withCategoryId(Integer categoryId) {
            return new  RequestAdvertisementDto(
                    categoryId,
                    price,
                    title,
                    image,
                    description,
                    active,
                    location
            );
        }
    }