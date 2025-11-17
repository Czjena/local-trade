package io.github.adrian.wieczorek.local_trade.service.user.dto;


public record UserDashboardResponseDto(String email, int ratingCount, double averageRating)  {
}
