package io.github.czjena.local_trade.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super("User Not Found:" + message);
    }
}
