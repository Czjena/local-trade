package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Image;
import io.github.czjena.local_trade.response.ResponseAdvertisementDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AdvertisementDtoMapper {

    @Mapping(source = "category", target = "categoryId", qualifiedByName = "categoryToId")
    @Mapping(source = "images", target = "imageUrls", qualifiedByName = "mapImagesToUrls")
    @Mapping(source = "images", target = "thumbnailUrls", qualifiedByName = "mapImagesToThumbnailUrls")
    ResponseAdvertisementDto toResponseAdvertisementDto(Advertisement advertisement);

    @Named("categoryToId")
    default Integer categoryToId(Category category) {
        return category != null ? category.getId() : null;
    }

    @Named("mapImagesToUrls")
    default List<String> mapImagesToUrls(List<Image> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        } else {
            return images.stream()
                    .map(Image::getUrl)
                    .collect(Collectors.toList());
        }
    }

    @Named("mapImagesToThumbnailUrls")
    default List<String> mapImagesToThumbnailUrls(List<Image> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        } else {
            return images.stream()
                    .map(Image::getThumbnailUrl)
                    .collect(Collectors.toList());
        }
    }
}