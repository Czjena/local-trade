package io.github.adrian.wieczorek.local_trade.unit;

import io.github.adrian.wieczorek.local_trade.dto.AdvertisementUpdateDto;
import io.github.adrian.wieczorek.local_trade.mappers.AdvertisementDtoMapper;
import io.github.adrian.wieczorek.local_trade.mappers.AdvertisementMapper;
import io.github.adrian.wieczorek.local_trade.mappers.SimpleAdvertisementDtoMapper;
import io.github.adrian.wieczorek.local_trade.model.AdvertisementEntity;
import io.github.adrian.wieczorek.local_trade.model.CategoryEntity;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import io.github.adrian.wieczorek.local_trade.repository.AdvertisementRepository;
import io.github.adrian.wieczorek.local_trade.repository.CategoryRepository;
import io.github.adrian.wieczorek.local_trade.repository.UsersRepository;
import io.github.adrian.wieczorek.local_trade.request.RequestAdvertisementDto;
import io.github.adrian.wieczorek.local_trade.response.ResponseAdvertisementDto;
import io.github.adrian.wieczorek.local_trade.response.SimpleAdvertisementResponseDto;
import io.github.adrian.wieczorek.local_trade.service.business.AdvertisementServiceImpl;
import io.github.adrian.wieczorek.local_trade.testutils.AdUtils;
import io.github.adrian.wieczorek.local_trade.testutils.CategoryUtils;
import io.github.adrian.wieczorek.local_trade.testutils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AdUnitTests {

    @Mock
    private AdvertisementRepository advertisementRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private AdvertisementServiceImpl advertisementService;

    @Mock
    private AdvertisementMapper advertisementMapper;
    @Mock
    SimpleAdvertisementDtoMapper simpleAdvertisementDtoMapper;
    @Mock
    AdvertisementDtoMapper advertisementDtoMapper;



    @Test
    void createAdvertisement_thenAdvertisementIsCreated() {
        UsersEntity user = UserUtils.createUserRoleUser();

        CategoryEntity categoryEntity = CategoryEntity.builder()
                .Id(2)
                .name("Car")
                .description("Car")
                .parentCategory("Vehicle")
                .build();

        RequestAdvertisementDto ad = getRequestAdvertisementDto();


        UserDetails userDetails = mock(UserDetails.class);
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));

        when(categoryRepository.findById(any())).thenReturn(Optional.of(categoryEntity));



        AdvertisementEntity mapped = AdvertisementEntity.builder()
                .categoryEntity(categoryEntity)
                .price(ad.price())
                .title(ad.title())
                .image(ad.image())
                .description(ad.description())
                .active(ad.active())
                .location(ad.location())
                .build();

        var simpleAdvertisementResponseDto = new SimpleAdvertisementResponseDto(
                UUID.randomUUID(),ad.title()
        );

        when(simpleAdvertisementDtoMapper.advertisementToSimpleDto(mapped)).thenReturn(simpleAdvertisementResponseDto);

        // stubowanie repozytorium
        when(advertisementRepository.save(any(AdvertisementEntity.class))).thenReturn(mapped);

        // wywołanie metody serwisu
        SimpleAdvertisementResponseDto created = advertisementService.addAd(ad,userDetails);

        // asercje
        Assertions.assertNotNull(created);
        Assertions.assertEquals(ad.title(), created.title());

        // sprawdzenie czy save() było wywołane dokładnie raz
        verify(advertisementRepository, times(1)).save(any(AdvertisementEntity.class));

    }

    private static @NotNull RequestAdvertisementDto getRequestAdvertisementDto() {
        BigDecimal price = new BigDecimal("149.99");
        RequestAdvertisementDto ad = new RequestAdvertisementDto(
                2,                      // category
                price,                      // price
                "Audi A4 B6",               // title
                "audi_a4.jpg",              // image
                "Well maintained, 1.9 TDI", // description
                true,                       // active
                "Warsaw"                    // location
        );
        return ad;
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

        var responseAdvertisementDto = advertisementService.getAdvertisementById(advertisementEntity.getAdvertisementId());

        Assertions.assertNotNull(responseAdvertisementDto);
        Assertions.assertEquals(advertisementEntity.getAdvertisementId(),responseAdvertisementDto.advertisementId());
        Assertions.assertEquals(advertisementEntity.getPrice(),responseAdvertisementDto.price());
    }
    @Test
    void getAdvertisementById_thenAdvertisementIsNotFound() {
        UUID advertisementId = UUID.randomUUID();
        when(advertisementRepository.findByAdvertisementId(advertisementId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> advertisementService.getAdvertisementById(advertisementId));
    }
    @Test
    void changeAdvertisement_callsMapperAndSaves() {
        UsersEntity user = UserUtils.createUserRoleUser();
        AdvertisementEntity ad = AdUtils.createAdvertisement();
        ad.setUser(user);
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        AdvertisementUpdateDto dto = new AdvertisementUpdateDto(null, null, null, null, null);
        when(advertisementRepository.findByUserAndId(user, ad.getId()))
                .thenReturn(Optional.of(ad));

        advertisementService.changeAdvertisement(dto, userDetails, ad.getId());
        verify(advertisementMapper).updateAdvertisementFromDtoSkipNull(dto, ad);
        verify(advertisementRepository).save(ad);
    }
    @Test
    void deleteAdvertisement_callsRepository() {
        UsersEntity user = UserUtils.createUserRoleUser();
        AdvertisementEntity ad = AdUtils.createAdvertisement();
        ad.setUser(user);
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(advertisementRepository.findByUserAndId(user, ad.getId()))
                .thenReturn(Optional.of(ad));
        advertisementService.deleteAdvertisement(userDetails, ad.getId());
        verify(advertisementRepository).delete(ad);
    }
    @Test
    void deleteAdvertisement_throwsEntityNotFoundException() {
        UsersEntity user = UserUtils.createUserRoleUser();
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        int id = 999;
        when(advertisementRepository.findByUserAndId(user, id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> advertisementService.deleteAdvertisement(userDetails, id));
    }

}
