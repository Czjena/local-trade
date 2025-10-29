package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.CategoryDto;
import io.github.czjena.local_trade.model.Advertisement;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {
    @Transactional
    List<Advertisement> findAllAdvertisementsByCategoryId(Integer categoryId);

    @Transactional
    String getCategoryNameForEndPoints(Integer categoryId);

    @Transactional
    Integer getCategoryIdForEndPointsFromAdvertisement(Integer advertisementId);

    @Cacheable("categories")
    @Transactional
    List<CategoryDto> getAllCategories();

    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    CategoryDto postCategory(CategoryDto category);

    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    CategoryDto changeCategory(Integer id, CategoryDto categoryDto);

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    void deleteCategory(Integer categoryId);
}
