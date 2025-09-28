package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.model.Advertisement;

import java.math.BigDecimal;

public class AdUtils {
    public static Advertisement createAdvertisement() {
        BigDecimal price = new BigDecimal("149.99");
        return Advertisement.builder()
                .id(1)
            .title("test")
            .description("test")
            .price(price)
            .active(true)
            .location("test")
            .build();

    }
}
