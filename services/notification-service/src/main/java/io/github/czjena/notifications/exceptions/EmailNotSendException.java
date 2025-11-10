package io.github.czjena.notifications.exceptions;

public class EmailNotSendException extends RuntimeException {
    public EmailNotSendException(String message,Throwable cause) {
        super(message);
    }
}
