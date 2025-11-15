package io.github.adrian.wieczorek.local_trade.unit;

import io.github.adrian.wieczorek.local_trade.service.category.dto.CategoryDto;
import io.github.adrian.wieczorek.local_trade.service.category.mapper.CategoryMapper;
import io.github.adrian.wieczorek.local_trade.service.advertisement.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.service.category.CategoryEntity;
import io.github.adrian.wieczorek.local_trade.service.advertisement.AdvertisementRepository;
import io.github.adrian.wieczorek.local_trade.service.category.CategoryRepository;
import io.github.adrian.wieczorek.local_trade.service.category.service.CategoryServiceImpl;
import io.github.adrian.wieczorek.local_trade.testutils.AdUtils;
import io.github.adrian.wieczorek.local_trade.testutils.CategoryUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryEntityTests {

    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private AdvertisementRepository advertisementRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    CategoryMapper categoryMapper;

    @Test
    public void findAdvertisementsByCategoryId_thenReturnAllAdvertisements() {
        CategoryEntity categoryEntity = CategoryUtils.createCategory();
        AdvertisementEntity advertisementEntity = AdUtils.createAdvertisement();

        when(categoryRepository.findById(categoryEntity.getId())).thenReturn(Optional.of(categoryEntity));
        when(advertisementRepository.findByCategoryEntityId(categoryEntity.getId())).thenReturn(List.of(advertisementEntity));

        List<AdvertisementEntity> advertisementEntities = categoryService.findAllAdvertisementsByCategoryId(categoryEntity.getId());

        Assertions.assertEquals(advertisementEntities, List.of(advertisementEntity));

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
        CategoryEntity categoryEntity = CategoryUtils.createCategory();
        List<AdvertisementEntity> advertisementEntity = List.of();
        when(categoryRepository.findById(categoryEntity.getId())).thenReturn(Optional.of(categoryEntity));
        when(advertisementRepository.findByCategoryEntityId(categoryEntity.getId())).thenReturn(advertisementEntity);
        List<AdvertisementEntity> advertisementEntities = categoryService.findAllAdvertisementsByCategoryId(categoryEntity.getId());
        Assertions.assertEquals(advertisementEntities, advertisementEntity);
    }

    @Test
    public void postCategoryId_thenReturnCategoryNameForEndPoints() {
        CategoryEntity categoryEntity = CategoryUtils.createCategory();
        when(categoryRepository.findById(categoryEntity.getId())).thenReturn(Optional.of(categoryEntity));
        String categoryName = categoryService.getCategoryNameForEndPoints(categoryEntity.getId());
        Assertions.assertEquals(categoryName, categoryEntity.getName());
    }

    @Test
    public void postNonExistingCategoryId_thenReturnException() {
        Integer nonExistingCategoryId = 9999;
        when(categoryRepository.findById(nonExistingCategoryId)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.getCategoryNameForEndPoints(nonExistingCategoryId);
        });
    }

    @Test
    public void getCategoryIdForEndPointsFromAdvertisement_thenReturnCategoryId() {
        AdvertisementEntity advertisementEntity = AdUtils.createAdvertisement();
        CategoryEntity categoryEntity = CategoryUtils.createCategory();
        advertisementEntity.setCategoryEntity(categoryEntity);
        when(advertisementRepository.findById(advertisementEntity.getId())).thenReturn(Optional.of(advertisementEntity));
        Integer categoryId = categoryService.getCategoryIdForEndPointsFromAdvertisement(advertisementEntity.getId());
        Assertions.assertEquals(categoryId, categoryEntity.getId());
    }

    @Test
    public void getCategoryIdForEndPointsFromAdvertisement_thenReturnException() {
        Integer nonExistingCategoryId = 9999;
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.getCategoryIdForEndPointsFromAdvertisement(nonExistingCategoryId);
        });
    }

    @Test
    public void postCategory_thenCategoryIsSaved() {
        CategoryDto dto = CategoryUtils.createCategoryDto();
        CategoryEntity savedCategoryEntity = new CategoryEntity(
                1,
                "Test category",
                "Category for testing",
                "Test parent category",
                UUID.randomUUID()
        );
        CategoryDto savedCategoryDto = new CategoryDto(
                1,
                "Test category",
                "Category for testing",
                "Test parent category"
        );


        ArgumentCaptor<CategoryEntity> captor = ArgumentCaptor.forClass(CategoryEntity.class);

        when(categoryRepository.save(captor.capture())).thenReturn(savedCategoryEntity);


        when(categoryMapper.postCategoryToDto(savedCategoryEntity)).thenReturn(savedCategoryDto);
        CategoryDto result = categoryService.postCategory(dto);

        verify(categoryRepository, times(1)).save(captor.capture());

        verify(categoryMapper, times(1)).postCategoryToDto(savedCategoryEntity);


        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.id());
        Assertions.assertEquals("Test category", result.name());
        Assertions.assertEquals("Category for testing", result.description());
        Assertions.assertEquals("Test parent category", result.parentCategory());

    }

    @Test
    public void postDuplicatedCategory_thenCategoryIsNotSaved() {
        CategoryDto dto = CategoryUtils.createCategoryDto();

        when(categoryRepository.save(any(CategoryEntity.class))).thenThrow(new DataIntegrityViolationException("Duplicated name "));

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            categoryService.postCategory(dto);
        });

        verify(categoryRepository, times(1)).save(any(CategoryEntity.class));
        verify(categoryMapper, never()).postCategoryToDto(any(CategoryEntity.class));
    }

    @Test
    public void changeCategory_thenCategoryIsChanged() {
        UUID categoryId = UUID.randomUUID();
        CategoryDto inputDto = new CategoryDto(
                null,
                "Changed text",
                "Changed text",
                null
        );

        CategoryEntity oldEntityInDb = new CategoryEntity(
                1,
                "Sample text",
                "Sample text",
                "Sample text",
                categoryId
        );

        CategoryEntity expectedEntityInDb = new CategoryEntity(
                1,
                "Changed text",
                "Changed text",
                "Sample text",
                categoryId
        );

        CategoryDto updatedDto = new CategoryDto(
                1,
                "Changed text",
                "Changed text",
                "Sample text"
        );

        ArgumentCaptor<CategoryEntity> captor = ArgumentCaptor.forClass(CategoryEntity.class);

        when(categoryRepository.findById(oldEntityInDb.getId())).thenReturn(Optional.of(oldEntityInDb));
        when(categoryRepository.save(any(CategoryEntity.class))).then(invocation -> invocation.getArgument(0));
        when(categoryMapper.postCategoryToDto(any(CategoryEntity.class))).thenReturn(updatedDto);

        CategoryDto result = categoryService.changeCategory(1, inputDto);
        verify(categoryRepository, times(1)).save((captor.capture()));
        verify(categoryMapper, times(1)).postCategoryToDto(any(CategoryEntity.class));

        CategoryEntity capturedEntity = captor.getValue();


        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, capturedEntity.getId());
        Assertions.assertEquals("Changed text", capturedEntity.getName());
        Assertions.assertEquals("Changed text", capturedEntity.getDescription());
        Assertions.assertEquals("Sample text", capturedEntity.getParentCategory());

    }

    @Test
    public void changeCategoryWithInvalidId_thenCategoryIsNotChanged() {
        Integer categoryId = 9999;
        CategoryDto dto = CategoryUtils.createCategoryDto();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.changeCategory(categoryId, dto);});
        verify(categoryRepository ,never()).save(any(CategoryEntity.class));
    }

    @Test
    public void deleteCategory_thenCategoryIsDeleted() {
        CategoryEntity oldEntityInDb = CategoryUtils.createCategory();
        oldEntityInDb.setId(1);
        when(categoryRepository.existsById(oldEntityInDb.getId())).thenReturn(true);
        when(advertisementRepository.countByCategoryEntityId(oldEntityInDb.getId())).thenReturn(0L);
        categoryService.deleteCategory(oldEntityInDb.getId());
        verify(categoryRepository, times(1)).deleteById(oldEntityInDb.getId());

    }
    @Test
    public void deleteCategoryWithInvalidId_thenCategoryIsNotDeleted() {
        Integer categoryId = 9999;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.deleteCategory(categoryId);});
        verify(advertisementRepository, never()).countByCategoryEntityId(categoryId);
        verify(categoryRepository, never()).deleteById(anyInt());
    }

    @Test
    public void deleteCategoryWithAdvertsAssigned_thenCategoryIsNotDeleted() {
        Integer categoryId = 1;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(advertisementRepository.countByCategoryEntityId(categoryId)).thenReturn(5L);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.deleteCategory(categoryId);});

        verify(categoryRepository, never()).deleteById(anyInt());
    }
}
