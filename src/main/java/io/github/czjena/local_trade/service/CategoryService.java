package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.CategoryDto;
import io.github.czjena.local_trade.model.Advertisement;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {
    @Transactional(readOnly = true)
    List<Advertisement> findAllAdvertisementsByCategoryId(Integer categoryId);

    @Transactional(readOnly = true)
    String getCategoryNameForEndPoints(Integer categoryId);

    @Transactional(readOnly = true)
    Integer getCategoryIdForEndPointsFromAdvertisement(Integer advertisementId);

    @Transactional(readOnly = true)
    List<CategoryDto> getAllCategories();

    @Transactional
    CategoryDto postCategory(CategoryDto category);

    @Transactional
    CategoryDto changeCategory(Integer id, CategoryDto categoryDto);

    @Transactional
    void deleteCategory(Integer categoryId);
}
