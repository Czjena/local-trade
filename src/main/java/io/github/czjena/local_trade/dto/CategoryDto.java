package io.github.czjena.local_trade.dto;

import io.github.czjena.local_trade.model.Category;

public record CategoryDto (
        Integer id,
        String name,
        String description,
        String parentCategory
) {

}
