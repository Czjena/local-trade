package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.enums.SortDirection;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;

import java.math.BigDecimal;

import static io.github.czjena.local_trade.enums.AdvertisementSortField.TITLE;
import static java.lang.Long.valueOf;

public class AdFiltersUtils {
    public static AdvertisementFilterDto getAdvertisementFilterDto() {
        return new AdvertisementFilterDto(3,new BigDecimal(3),new BigDecimal(1000),"test location","test title", true);
    }
    public static AdvertisementFilterDto filterByCategory(Integer categoryId) {
        return new AdvertisementFilterDto(
                categoryId,
                null,
                null,
                null,
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
                null
        );
    }

    public static Advertisement createAdvertisementWithIndex(Category category, Users user, int index) {
        return Advertisement.builder()
                .title("Test Advertisement " + index) // Każdy tytuł będzie unikalny
                .description("Some description")
                .price(BigDecimal.valueOf(100 + index * 10L))
                .location("location")// Każda cena będzie inna
                .category(category)
                .user(user)
                .build();
    }
}

