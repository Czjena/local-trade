package io.github.adrian.wieczorek.local_trade.unit;

import io.github.adrian.wieczorek.local_trade.service.advertisement.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.service.advertisement.AdvertisementRepository;
import io.github.adrian.wieczorek.local_trade.service.advertisement.dto.ResponseAdvertisementDto;
import io.github.adrian.wieczorek.local_trade.service.advertisement.mapper.AdvertisementDtoMapper;
import io.github.adrian.wieczorek.local_trade.service.advertisement.service.AdvertisementFinder;
import io.github.adrian.wieczorek.local_trade.service.advertisement.service.AdvertisementService;
import io.github.adrian.wieczorek.local_trade.testutils.AdUtils;
import io.github.adrian.wieczorek.local_trade.testutils.CategoryUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdvertisementFinderUnitTests {
    @InjectMocks
    private AdvertisementFinder advertisementFinder;
    @Mock
    private AdvertisementRepository advertisementRepository;
    @Mock
    private AdvertisementDtoMapper advertisementDtoMapper;

    @Test
    void getAdvertisementById_thenAdvertisementIsNotFound() {
        UUID advertisementId = UUID.randomUUID();
        when(advertisementRepository.findByAdvertisementId(advertisementId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> advertisementFinder.getAdvertisementById(advertisementId));
    }
    @Test
    void getAdvertisementById_thenAdvertisementIsReturned() {
        AdvertisementEntity advertisementEntity = AdUtils.createAdvertisement();
        var category = CategoryUtils.createCategory();
        advertisementEntity.setCategoryEntity(category);
        advertisementEntity.setAdvertisementId(UUID.randomUUID());

        var mockResponseDto = new ResponseAdvertisementDto(
                advertisementEntity.getAdvertisementId(),
                advertisementEntity.getCategoryEntity().getId(),
                advertisementEntity.getPrice(),
                advertisementEntity.getTitle(),
                advertisementEntity.getImage(),
                advertisementEntity.getDescription(),
                advertisementEntity.isActive(),
                advertisementEntity.getLocation(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        when(advertisementDtoMapper.toResponseAdvertisementDto(advertisementEntity)).thenReturn(mockResponseDto);
        when(advertisementRepository.findByAdvertisementId(advertisementEntity.getAdvertisementId())).thenReturn(Optional.of(advertisementEntity));

        var responseAdvertisementDto = advertisementFinder.getAdvertisementById(advertisementEntity.getAdvertisementId());

        Assertions.assertNotNull(responseAdvertisementDto);
        Assertions.assertEquals(advertisementEntity.getAdvertisementId(),responseAdvertisementDto.advertisementId());
        Assertions.assertEquals(advertisementEntity.getPrice(),responseAdvertisementDto.price());
    }


}
