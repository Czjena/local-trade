package io.github.adrian.wieczorek.local_trade.service.infrastructure;

import io.github.adrian.wieczorek.local_trade.dto.CategoryDto;
import io.github.adrian.wieczorek.local_trade.model.AdvertisementEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {
    @Transactional(readOnly = true)
    List<AdvertisementEntity> findAllAdvertisementsByCategoryId(Integer categoryId);

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
