package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.response.SimpleUserResponseDto;
import org.springframework.stereotype.Component;

public class SimpleUserResponseDtoUtils {
    Integer id = 1;
    String name = "buyer";

    public SimpleUserResponseDtoUtils withId(Integer id) {
        this.id = id;
        return this;
    }
    public SimpleUserResponseDtoUtils withName(String name) {
        this.name = name;
        return this;
    }
    public SimpleUserResponseDto build(){
        return new SimpleUserResponseDto(id,name);
    }
}
