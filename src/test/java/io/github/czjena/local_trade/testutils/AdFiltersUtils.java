package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.dto.AdvertisementFilterDto;

import java.math.BigDecimal;

import static java.lang.Long.valueOf;

public class AdFiltersUtils {
    public static AdvertisementFilterDto getAdvertisementFilterDto() {
        return new AdvertisementFilterDto(3L,new BigDecimal(3),new BigDecimal(1000),"test location","test title", "test description",true,4,10,"category","ascending");
    }
}
