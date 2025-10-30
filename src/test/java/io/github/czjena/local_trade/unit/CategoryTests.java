package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.dto.CategoryDto;
import io.github.czjena.local_trade.mappers.CategoryMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.service.CategoryService;
import io.github.czjena.local_trade.service.CategoryServiceImpl;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.CategoryUtils;
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
public class CategoryTests {

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
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.getCategoryNameForEndPoints(nonExistingCategoryId);
        });
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
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.getCategoryIdForEndPointsFromAdvertisement(nonExistingCategoryId);
        });
    }

    @Test
    public void postCategory_thenCategoryIsSaved() {
        CategoryDto dto = CategoryUtils.createCategoryDto();
        Category savedCategory = new Category(
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


        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

        when(categoryRepository.save(captor.capture())).thenReturn(savedCategory);


        when(categoryMapper.postCategoryToDto(savedCategory)).thenReturn(savedCategoryDto);
        CategoryDto result = categoryService.postCategory(dto);

        verify(categoryRepository, times(1)).save(captor.capture());

        verify(categoryMapper, times(1)).postCategoryToDto(savedCategory);


        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.id());
        Assertions.assertEquals("Test category", result.name());
        Assertions.assertEquals("Category for testing", result.description());
        Assertions.assertEquals("Test parent category", result.parentCategory());

    }

    @Test
    public void postDuplicatedCategory_thenCategoryIsNotSaved() {
        CategoryDto dto = CategoryUtils.createCategoryDto();

        when(categoryRepository.save(any(Category.class))).thenThrow(new DataIntegrityViolationException("Duplicated name "));

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            categoryService.postCategory(dto);
        });

        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryMapper, never()).postCategoryToDto(any(Category.class));
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

        Category oldEntityInDb = new Category(
                1,
                "Sample text",
                "Sample text",
                "Sample text",
                categoryId
        );

        Category expectedEntityInDb = new Category(
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

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

        when(categoryRepository.findById(oldEntityInDb.getId())).thenReturn(Optional.of(oldEntityInDb));
        when(categoryRepository.save(any(Category.class))).then(invocation -> invocation.getArgument(0));
        when(categoryMapper.postCategoryToDto(any(Category.class))).thenReturn(updatedDto);

        CategoryDto result = categoryService.changeCategory(1, inputDto);
        verify(categoryRepository, times(1)).save((captor.capture()));
        verify(categoryMapper, times(1)).postCategoryToDto(any(Category.class));

        Category capturedEntity = captor.getValue();


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
        verify(categoryRepository ,never()).save(any(Category.class));
    }

    @Test
    public void deleteCategory_thenCategoryIsDeleted() {
        Category oldEntityInDb = CategoryUtils.createCategory();
        oldEntityInDb.setId(1);
        when(categoryRepository.existsById(oldEntityInDb.getId())).thenReturn(true);
        when(advertisementRepository.countByCategoryId(oldEntityInDb.getId())).thenReturn(0L);
        categoryService.deleteCategory(oldEntityInDb.getId());
        verify(categoryRepository, times(1)).deleteById(oldEntityInDb.getId());

    }
    @Test
    public void deleteCategoryWithInvalidId_thenCategoryIsNotDeleted() {
        Integer categoryId = 9999;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.deleteCategory(categoryId);});
        verify(advertisementRepository, never()).countByCategoryId(categoryId);
        verify(categoryRepository, never()).deleteById(anyInt());
    }

    @Test
    public void deleteCategoryWithAdvertsAssigned_thenCategoryIsNotDeleted() {
        Integer categoryId = 1;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(advertisementRepository.countByCategoryId(categoryId)).thenReturn(5L);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.deleteCategory(categoryId);});

        verify(categoryRepository, never()).deleteById(anyInt());
    }
}
