package io.github.adrian.wieczorek.local_trade.mappers;

import io.github.adrian.wieczorek.local_trade.model.ReviewEntity;
import io.github.adrian.wieczorek.local_trade.response.ReviewResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewResponseDtoMapper {
    ReviewResponseDto toDto(ReviewEntity reviewEntity);
}
