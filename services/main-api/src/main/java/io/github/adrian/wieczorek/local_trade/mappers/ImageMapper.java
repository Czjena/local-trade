package io.github.adrian.wieczorek.local_trade.mappers;

import io.github.adrian.wieczorek.local_trade.dto.ImageDto;
import io.github.adrian.wieczorek.local_trade.model.ImageEntity;

public class ImageMapper {
    public static ImageDto ImagetoImageDto(ImageEntity imageEntity) {
    return new ImageDto(imageEntity.getImageId(), imageEntity.getUrl(), imageEntity.getThumbnailUrl(), imageEntity.getSize(), imageEntity.getContentType());
    }
}
