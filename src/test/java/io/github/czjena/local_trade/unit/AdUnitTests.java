package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.dto.AdvertisementUpdateDto;
import io.github.czjena.local_trade.mappers.AdvertisementMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.request.RequestAdvertisementDto;
import io.github.czjena.local_trade.service.AdvertisementService;
import io.github.czjena.local_trade.service.AdvertisementServiceImpl;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;


import java.math.BigDecimal;
import java.util.Optional;

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



    @Test
    void createAdvertisement_thenAdvertisementIsCreated() {
        Users user = UserUtils.createUserRoleUser();

        Category category = Category.builder()
                .Id(2)
                .name("Car")
                .description("Car")
                .parentCategory("Vehicle")
                .build();

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

        UserDetails userDetails = mock(UserDetails.class);
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));

        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));

        Advertisement mapped = Advertisement.builder()
                .category(category)
                .price(ad.price())
                .title(ad.title())
                .image(ad.image())
                .description(ad.description())
                .active(ad.active())
                .location(ad.location())
                .build();

        // stubowanie repozytorium
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(mapped);

        // wywołanie metody serwisu
        Advertisement created = advertisementService.addAd(ad,userDetails);

        // asercje
        assertEquals(mapped, created);

        // sprawdzenie czy save() było wywołane dokładnie raz
        verify(advertisementRepository, times(1)).save(any(Advertisement.class));

    }
    @Test
    void getAdvertisementById_thenAdvertisementIsReturned() {
        Advertisement advertisement = AdUtils.createAdvertisement();

        when(advertisementRepository.findById(any())).thenReturn(Optional.of(advertisement));

        Advertisement ad = advertisementService.getAdvertisementById(1);

        assertEquals(advertisement, ad);
    }
    @Test
    void getAdvertisementById_thenAdvertisementIsNotFound() {
        int id = 999;
        when(advertisementRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> advertisementService.getAdvertisementById(id));
    }
    @Test
    void changeAdvertisement_callsMapperAndSaves() {
        Users user = UserUtils.createUserRoleUser();
        Advertisement ad = AdUtils.createAdvertisement();
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
        Users user = UserUtils.createUserRoleUser();
        Advertisement ad = AdUtils.createAdvertisement();
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
        Users user = UserUtils.createUserRoleUser();
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(usersRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        int id = 999;
        when(advertisementRepository.findByUserAndId(user, id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> advertisementService.deleteAdvertisement(userDetails, id));
    }

}
