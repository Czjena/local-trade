package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.enums.SortDirection;

import java.math.BigDecimal;

import static io.github.czjena.local_trade.enums.AdvertisementSortField.TITLE;
import static java.lang.Long.valueOf;

public class AdFiltersUtils {
    public static AdvertisementFilterDto getAdvertisementFilterDto() {
        return new AdvertisementFilterDto(3,new BigDecimal(3),new BigDecimal(1000),"test location","test title", "test description",true,null,null);
    }
    public static AdvertisementFilterDto filterByCategory(Integer categoryId) {
        return new AdvertisementFilterDto(
                categoryId,
                null,
                null,
                null,
                null,
                null,
                true,
                null,
                null
        );
    }
    public static AdvertisementFilterDto filterByTitle(String title) {
        return new AdvertisementFilterDto(
                null,
                null,
                null,
                null,
                title,
                null,
                true,
                null,
                null
        );
    }
    public static AdvertisementFilterDto filterByTitleAndCategoryAndMaxPrice(String title, BigDecimal maxPrice,Integer categoryId) {
        return new AdvertisementFilterDto(
                categoryId,
                null,
                maxPrice,
                null,
                title,
                null,
                true,
                null,
                null
        );
    }
}

