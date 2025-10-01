package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.dto.AdvertisementUpdateDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;

import java.math.BigDecimal;

public class AdUtils {
    public static Advertisement createAdvertisement() {
        BigDecimal price = new BigDecimal("149.99");
        return Advertisement.builder()
                .id(1)
                .title("test")
                .description("test")
                .image("test")
                .price(price)
                .active(true)
                .location("test")
                .build();

    }

    public static Advertisement createAdvertisementRoleUserForIntegrationTests(Category category, Users user) {
        BigDecimal price = new BigDecimal("149.99");
        return Advertisement.builder()
                .title("title test")
                .description("description test")
                .price(price)
                .active(true)
                .location("location test")
                .user(user)
                .category(category)
                .build();
    }

    public static AdvertisementUpdateDto createAdvertisementUpdateDto() {
        BigDecimal price = new BigDecimal("149.99");
        return new AdvertisementUpdateDto(price, "title update test", "title description test", "location test ", "image test");
    }
}