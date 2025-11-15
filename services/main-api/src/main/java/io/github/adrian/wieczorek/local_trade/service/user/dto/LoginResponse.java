package io.github.adrian.wieczorek.local_trade.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private long expiresIn;
    private String refreshToken;

}