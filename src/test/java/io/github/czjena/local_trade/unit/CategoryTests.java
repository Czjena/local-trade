package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.service.CategoryService;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.CategoryUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryTests {

    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private AdvertisementRepository advertisementRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    public void findAdvertisementsByCategoryId_thenReturnAllAdvertisements() {
        Category category = CategoryUtils.createCategory();
        Advertisement advertisement = AdUtils.createAdvertisement();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(advertisementRepository.findByCategoryId(category.getId())).thenReturn(List.of(advertisement));

        List<Advertisement> advertisements = categoryService.findAllAdvertisementsByCategoryId(category.getId());

        Assertions.assertEquals(advertisements, List.of(advertisement));

    }

    @Test
    public void findNonExistingAdvertisementsByCategoryId_thenReturnEmptyList() {
        Integer nonExistingCategoryId = 9999;
        when(categoryRepository.findById(nonExistingCategoryId)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.findAllAdvertisementsByCategoryId(nonExistingCategoryId);
        });
    }
    @Test
    public void emptyAdvertisementList_thenReturnEmptyList() {
        Category category = CategoryUtils.createCategory();
        List<Advertisement> advertisement = List.of();
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(advertisementRepository.findByCategoryId(category.getId())).thenReturn(advertisement);
        List<Advertisement> advertisements = categoryService.findAllAdvertisementsByCategoryId(category.getId());
        Assertions.assertEquals(advertisements, advertisement);
    }
    @Test
    public void postCategoryId_thenReturnCategoryNameForEndPoints() {
        Category category = CategoryUtils.createCategory();
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        String categoryName = categoryService.getCategoryNameForEndPoints(category.getId());
        Assertions.assertEquals(categoryName, category.getName());
    }
    @Test
    public void postNonExistingCategoryId_thenReturnException() {
        Integer nonExistingCategoryId = 9999;
        when(categoryRepository.findById(nonExistingCategoryId)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> { categoryService.getCategoryNameForEndPoints(nonExistingCategoryId); });
    }
    @Test
    public void getCategoryIdForEndPointsFromAdvertisement_thenReturnCategoryId() {
        Advertisement advertisement = AdUtils.createAdvertisement();
        Category category = CategoryUtils.createCategory();
        advertisement.setCategory(category);
        when(advertisementRepository.findById(advertisement.getId())).thenReturn(Optional.of(advertisement));
        Integer categoryId = categoryService.getCategoryIdForEndPointsFromAdvertisement(advertisement.getId());
        Assertions.assertEquals(categoryId, category.getId());
    }
    @Test
    public void getCategoryIdForEndPointsFromAdvertisement_thenReturnException() {
        Integer nonExistingCategoryId = 9999;
        Assertions.assertThrows(EntityNotFoundException.class, () -> { categoryService.getCategoryIdForEndPointsFromAdvertisement(nonExistingCategoryId); });
    }
}
