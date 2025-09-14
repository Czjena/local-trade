package io.github.czjena.local_trade.response;

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