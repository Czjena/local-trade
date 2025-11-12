package io.github.adrian.wieczorek.local_trade.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestDto(
        @NotNull
        @JsonProperty("rating")
        @Min(1)
        @Max(5)
        Integer rating,
        String comment
) {
}
