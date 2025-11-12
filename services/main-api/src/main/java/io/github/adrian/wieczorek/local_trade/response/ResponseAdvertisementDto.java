package io.github.adrian.wieczorek.local_trade.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ResponseAdvertisementDto(
        UUID advertisementId,
        Integer categoryId,
        BigDecimal price,
        String title,
        String image,
        String description,
        boolean active,
        String location,
        List<String> imageUrls,
        List<String> thumbnailUrls
) {

}