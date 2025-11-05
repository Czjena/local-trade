package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.response.SimpleAdvertisementResponseDto;
import org.springframework.stereotype.Component;

import java.util.UUID;
public class SimpleAdvertisementResponseDtoUtils {
    UUID id = UUID.randomUUID();
    String title = "title";

    public SimpleAdvertisementResponseDtoUtils withId(UUID id) {
        this.id = id;
        return this;
    }
    public SimpleAdvertisementResponseDtoUtils withTitle(String title) {
        this.title = title;
        return this;
    }
    public SimpleAdvertisementResponseDto build() {
        return new SimpleAdvertisementResponseDto(id, title);
    }
}
