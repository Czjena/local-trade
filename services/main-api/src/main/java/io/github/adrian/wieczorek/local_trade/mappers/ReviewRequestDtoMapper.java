package io.github.adrian.wieczorek.local_trade.mappers;

import io.github.adrian.wieczorek.local_trade.model.ReviewEntity;
import io.github.adrian.wieczorek.local_trade.request.ReviewRequestDto;
import org.mapstruct.Mapper;

@Mapper
public interface ReviewRequestDtoMapper {
    ReviewRequestDto toDto(ReviewEntity reviewEntity);
}
