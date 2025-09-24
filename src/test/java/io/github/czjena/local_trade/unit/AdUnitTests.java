package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.service.AdvertisementService;
import io.github.czjena.local_trade.testutils.UserUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdUnitTests {

    @Mock
    private AdvertisementRepository advertisementRepository;

    @InjectMocks
    private AdvertisementService advertisementService;

    @Test
    void createAdvertisement_thenAdvertisementIsCreated() {
        Users user = UserUtils.createUserRoleUser();

        BigDecimal price = new BigDecimal("149.99");
        AdvertisementDto ad = new AdvertisementDto(
                "Car",                      // category
                price,                      // price
                "Audi A4 B6",               // title
                "audi_a4.jpg",              // image
                "Well maintained, 1.9 TDI", // description
                true,                       // active
                "Warsaw"                    // location
        );

        Advertisement mapped = Advertisement.builder()
                .category(ad.category())
                .price(ad.price())
                .title(ad.title())
                .image(ad.image())
                .description(ad.description())
                .active(ad.active())
                .location(ad.location())
                .build();

        // stubowanie repozytorium – najpierw!
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(mapped);

        // wywołanie metody serwisu
        Advertisement created = advertisementService.addAd(ad,user);

        // asercje
        assertEquals("Car", created.getCategory());
        assertEquals(new BigDecimal("149.99"), created.getPrice());
        assertEquals("Audi A4 B6", created.getTitle());
        assertEquals("Warsaw", created.getLocation());

        // sprawdzenie czy save() było wywołane dokładnie raz
        verify(advertisementRepository, times(1)).save(any(Advertisement.class));
    }
}