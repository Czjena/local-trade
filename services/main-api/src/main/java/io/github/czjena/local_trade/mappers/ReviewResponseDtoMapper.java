package io.github.czjena.local_trade.mappers;

import io.github.czjena.local_trade.model.Review;
import io.github.czjena.local_trade.response.ReviewResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewResponseDtoMapper {
    ReviewResponseDto toDto(Review review);
}
