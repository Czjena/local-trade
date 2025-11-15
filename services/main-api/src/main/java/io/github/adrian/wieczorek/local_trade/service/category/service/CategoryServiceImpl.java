package io.github.adrian.wieczorek.local_trade.service.category.service;

import io.github.adrian.wieczorek.local_trade.service.category.dto.CategoryDto;
import io.github.adrian.wieczorek.local_trade.service.category.mapper.CategoryMapper;
import io.github.adrian.wieczorek.local_trade.service.advertisement.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.service.category.CategoryEntity;
import io.github.adrian.wieczorek.local_trade.service.advertisement.AdvertisementRepository;
import io.github.adrian.wieczorek.local_trade.service.category.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final AdvertisementRepository advertisementRepository;
    private final CategoryMapper categoryMapper;


@Transactional(readOnly = true)
@Override
public List<AdvertisementEntity> findAllAdvertisementsByCategoryId(Integer categoryId) {
        if (categoryRepository.findById(categoryId).isPresent()) {
            return advertisementRepository.findByCategoryEntityId(categoryId);
        }
        throw new EntityNotFoundException("Category not found");
    }
    @Transactional(readOnly = true)
    @Override
    public String getCategoryNameForEndPoints(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .map(CategoryEntity::getName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    }
    @Transactional(readOnly = true)
    @Override
    public Integer getCategoryIdForEndPointsFromAdvertisement(Integer advertisementId) {
        return advertisementRepository.findById(advertisementId)
                .map(AdvertisementEntity::getCategoryEntity)
                .map(CategoryEntity::getId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    }
    @Cacheable("categories")
    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::postCategoryToDto)
                .toList();
    }
    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    @Override
    public CategoryDto postCategory(CategoryDto category) {
        CategoryEntity newCategoryEntity = new CategoryEntity();
        newCategoryEntity.setName(category.name());
        newCategoryEntity.setDescription(category.description());
        newCategoryEntity.setParentCategory(category.parentCategory());
        CategoryEntity savedCategoryEntity = categoryRepository.save(newCategoryEntity);
        return categoryMapper.postCategoryToDto(savedCategoryEntity);
    }
    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    @Override
    public CategoryDto changeCategory(Integer id, CategoryDto categoryDto) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        if(categoryDto.name() != null) {
            categoryEntity.setName(categoryDto.name());
        }
        if(categoryDto.description() != null) {
            categoryEntity.setDescription(categoryDto.description());
        }
        if(categoryDto.parentCategory() != null) {
            categoryEntity.setParentCategory(categoryDto.parentCategory());
        }
        CategoryEntity Saved = categoryRepository.save(categoryEntity);
        return categoryMapper.postCategoryToDto(Saved);
    }
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    @Override
    public void deleteCategory(Integer categoryId) {
        if(!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category not found");
        }
        long adCount = advertisementRepository.countByCategoryEntityId(categoryId);
        if (adCount == 0) {
            categoryRepository.deleteById(categoryId);
        }else {
            throw new EntityNotFoundException("Cannot delete category it is in use by:" + adCount + "advertisements");
        }
    }
}
