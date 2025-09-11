package io.github.czjena.local_trade.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private long expiresIn;

}