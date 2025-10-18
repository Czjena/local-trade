package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.CategoryDto;
import io.github.czjena.local_trade.mappers.CategoryMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AdvertisementRepository advertisementRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, AdvertisementRepository advertisementRepository, CategoryMapper categoryMapper) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }
@Transactional
    public List<Advertisement> findAllAdvertisementsByCategoryId(Integer categoryId) {
        if (categoryRepository.findById(categoryId).isPresent()) {
            return advertisementRepository.findByCategoryId(categoryId);
        }
        throw new EntityNotFoundException("Category not found");
    }
    @Transactional
    public String getCategoryNameForEndPoints(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    }
    @Transactional
    public Integer getCategoryIdForEndPointsFromAdvertisement(Integer advertisementId) {
        return advertisementRepository.findById(advertisementId)
                .map(Advertisement::getCategory)
                .map(Category::getId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    }
    @Transactional
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::postCategoryToDto)
                .toList();
    }
    @Transactional
    public CategoryDto postCategory(CategoryDto category) {
        Category newCategory = new Category();
        newCategory.setName(category.name());
        newCategory.setDescription(category.description());
        newCategory.setParentCategory(category.parentCategory());
         Category savedCategory = categoryRepository.save(newCategory);
         return categoryMapper.postCategoryToDto(savedCategory);
    }
    @Transactional
    public CategoryDto changeCategory(Integer id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        if(categoryDto.name() != null) {
            category.setName(categoryDto.name());
        }
        if(categoryDto.description() != null) {
            category.setDescription(categoryDto.description());
        }
        if(categoryDto.parentCategory() != null) {
            category.setParentCategory(categoryDto.parentCategory());
        }
         Category Saved = categoryRepository.save(category);
        return categoryMapper.postCategoryToDto(Saved);
    }
    @Transactional
    public void deleteCategory(Integer categoryId) {
        if(!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category not found");
        }
        long adCount = advertisementRepository.countByCategoryId(categoryId);
        if (adCount == 0) {
            categoryRepository.deleteById(categoryId);
        }else {
            throw new EntityNotFoundException("Cannot delete category it is in use by:" + adCount + "advertisements");
        }
    }
}
