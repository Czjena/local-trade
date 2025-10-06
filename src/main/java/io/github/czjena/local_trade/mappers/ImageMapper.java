package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.dto.ImageDto;
import io.github.czjena.local_trade.model.Image;

public class ImageMapper {
    public static ImageDto ImagetoImageDto(Image image) {
    return new ImageDto(image.getImageId(), image.getUrl(), image.getSize(), image.getContentType());
    }
}
