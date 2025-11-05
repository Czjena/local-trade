package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.model.Review;
import io.github.czjena.local_trade.request.ReviewRequestDto;
import org.mapstruct.Mapper;

@Mapper
public interface ReviewRequestDtoMapper {
    ReviewRequestDto toDto(Review review);
}
