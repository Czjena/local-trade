package io.github.czjena.notifications.unit;

import io.github.czjena.dtos.NotificationEvent;
import io.github.czjena.notifications.exceptions.EmailNotSendException;
import io.github.czjena.notifications.handlers.UserRegisteredHandler;
import io.github.czjena.notifications.service.infrastructure.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.Context;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRegisteredHandlerTests {
    @Mock
    private EmailService emailService;
    @InjectMocks
    private UserRegisteredHandler userRegisteredHandler;



    @Test
    public void whenGettingUserRegisteredHandler_shouldReturnEmailSent() {
        var context = Map.of(
                "userName","Test User",
                "userEmail","Test Email"
        );

        NotificationEvent notificationEvent = new NotificationEvent("USER_REGISTERED", UUID.randomUUID(),context);

        userRegisteredHandler.handle(notificationEvent);

        verify(emailService,times(1)).sendWelcomeEmail("Test Email","Test User");

    }

    @Test
    public void whenGettingUserRegisteredHandlerAndThereIsSmtpError_shouldThrowEmailNotSendException() {
        var context = Map.of(
                "userName","Test User",
                "userEmail","Test Email"
        );

        NotificationEvent notificationEvent = new NotificationEvent("USER_REGISTERED", UUID.randomUUID(),context);

        doThrow(new EmailNotSendException("SMTP Error",null)).when(emailService).sendWelcomeEmail("Test Email","Test User");

        Assertions.assertThrows(EmailNotSendException.class, () -> userRegisteredHandler.handle(notificationEvent));

        verify(emailService,times(1)).sendWelcomeEmail("Test Email","Test User");

    }

}
