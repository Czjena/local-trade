package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AdvertisementRepository advertisementRepository;

    public CategoryService(CategoryRepository categoryRepository, AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Advertisement> findAllAdvertisementsByCategoryId(Integer categoryId) {
        if (categoryRepository.findById(categoryId).isPresent()) {
            return advertisementRepository.findByCategoryId(categoryId);
        }
        throw new EntityNotFoundException("Category not found");
    }

    public String getCategoryNameForEndPoints(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    }
    public Integer getCategoryIdForEndPointsFromAdvertisement(Integer advertisementId) {
        return advertisementRepository.findById(advertisementId)
                .map(Advertisement::getCategory)
                .map(Category::getId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

    }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
