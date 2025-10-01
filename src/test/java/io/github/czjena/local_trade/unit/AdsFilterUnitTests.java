package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.service.AdvertisementFilterService;
import io.github.czjena.local_trade.testutils.AdFiltersUtils;
import io.github.czjena.local_trade.testutils.AdUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
        when(advertisementRepository.findAll(advertisementFilterDto, PageRequest.of(0, 10))).thenReturn(new PageImpl<>(advertisements));
        Pageable pageable = PageRequest.of(0, 10);
        Page<AdvertisementDto> result = advertisementFilterService.filterAndPageAdvertisements(advertisementFilterDto, pageable);

        assertEquals(3,result.getSize());
        assertEquals(1,result.getTotalPages());
        assertTrue(result.getContent().stream().allMatch(advertisementDto -> advertisementFilterDto.active()));
    }
}
