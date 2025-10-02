package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.service.AdvertisementFilterService;
import io.github.czjena.local_trade.testutils.AdFiltersUtils;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.CategoryUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.IntStream;

import static javax.management.Query.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdsFilterUnitTests {
    @Mock
    private AdvertisementRepository advertisementRepository;
    @InjectMocks
    private AdvertisementFilterService advertisementFilterService;
    @Test
    public void filterAndPageAdvertisements_thenReturnPageOfAdvertisements() {
        AdvertisementFilterDto advertisementFilterDto = AdFiltersUtils.getAdvertisementFilterDto();
        Advertisement ad1 = AdUtils.createAdvertisement();
        Advertisement ad2 = AdUtils.createAdvertisement();
        Advertisement ad3 = AdUtils.createAdvertisement();
        List<Advertisement> advertisements = List.of(ad1, ad2, ad3);
        Pageable pageable = PageRequest.of(0, 10);
        when(advertisementRepository.findAll(
                ArgumentMatchers.<Specification<Advertisement>>any(),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(advertisements));

        Page<AdvertisementDto> result = advertisementFilterService
                .filterAndPageAdvertisements(advertisementFilterDto, pageable);

        assertEquals(3, result.getNumberOfElements());
        assertEquals(1, result.getTotalPages());
        // Sprawdzamy, że wszystkie DTO mają aktywność zgodną z filtrem
        assertTrue(result.getContent().stream().allMatch(AdvertisementDto::active));
    }
    @Test
    public void filterByCategoryIdAndPageAdvertisements_thenReturnPageOfAdvertisements() {
        Category category = CategoryUtils.createCategory();
        AdvertisementFilterDto advertisementFilterDto = AdFiltersUtils.filterByCategory(category.getId());
        List<Advertisement> advertisements = IntStream.range(0, 10)
                .mapToObj(i->AdUtils.createAdvertisement())
                .toList();
        Pageable pageable = PageRequest.of(0, 10);
        when(advertisementRepository.findAll(ArgumentMatchers.any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(advertisements));

        Page<AdvertisementDto> result = advertisementFilterService.filterAndPageAdvertisements(advertisementFilterDto, pageable);

        assertEquals(10, result.getNumberOfElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.getContent().stream().allMatch(AdvertisementDto::active));

    }
   @Test
   public void filterByCategoryAndPageWithWrongDataAdvertisements_thenReturnNoAdvertisements() {
        int categoryId = 999;
        AdvertisementFilterDto advertisementFilterDto = AdFiltersUtils.filterByCategory(categoryId);
        List<Advertisement> ad = IntStream.range(0,10)
                .mapToObj(i->AdUtils.createAdvertisement())
                .toList();
        Pageable pageable = PageRequest.of(0, 10);
        when(advertisementRepository.findAll(ArgumentMatchers.any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

        Page<AdvertisementDto> result = advertisementFilterService.filterAndPageAdvertisements(advertisementFilterDto, pageable);

        assertEquals(0, result.getNumberOfElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.getContent().stream().allMatch(AdvertisementDto::active));
    }

}
