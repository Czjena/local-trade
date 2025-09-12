package io.github.czjena.local_trade.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserDto {
    @NotBlank
    @Email
    public String email;
    public String name;
    public String password;
}
