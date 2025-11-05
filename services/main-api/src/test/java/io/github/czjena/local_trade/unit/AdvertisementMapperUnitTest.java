package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.dto.AdvertisementUpdateDto;
import io.github.czjena.local_trade.mappers.AdvertisementMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.testutils.AdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@ExtendWith(MockitoExtension.class)
public class AdvertisementMapperUnitTest {
    @Test
    public void updateAdvertisementFromDtoSkipNull_thenReturnUpdatedAdvertisement() {
        AdvertisementUpdateDto dto = new AdvertisementUpdateDto(null, "test123", "test123", "test123", "test123");
        Advertisement advertisement = AdUtils.createAdvertisement();
        BigDecimal price = new BigDecimal("149.99");
        AdvertisementMapper mapper = Mappers.getMapper(AdvertisementMapper.class);
        mapper.updateAdvertisementFromDtoSkipNull(dto, advertisement);

        Assertions.assertEquals("test123", advertisement.getTitle());
        Assertions.assertEquals("test123", advertisement.getDescription());
        Assertions.assertEquals("test123", advertisement.getLocation());
        Assertions.assertEquals("test123", advertisement.getImage());
        Assertions.assertEquals(advertisement.getPrice(), price);
    }
}
