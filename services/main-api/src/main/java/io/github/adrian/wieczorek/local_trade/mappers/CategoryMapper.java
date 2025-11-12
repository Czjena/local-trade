package io.github.adrian.wieczorek.local_trade.mappers;

import io.github.adrian.wieczorek.local_trade.dto.CategoryDto;
import io.github.adrian.wieczorek.local_trade.model.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto postCategoryToDto(CategoryEntity categoryEntity);

    CategoryEntity postCategoryFromDto(CategoryDto categoryDto);

}