package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.dto.CategoryDto;
import io.github.czjena.local_trade.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto postCategoryToDto(Category category);

    Category postCategoryFromDto(CategoryDto categoryDto);

}