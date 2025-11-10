package io.github.czjena.notifications.handlers;

import io.github.czjena.dtos.NotificationEvent;
import io.github.czjena.notifications.service.infrastructure.EmailService;
import io.github.czjena.notifications.service.infrastructure.NotificationHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredHandler implements NotificationHandler {

    private final EmailService emailService;

    @Override
    public void handle(NotificationEvent notificationEvent) {
        String email = notificationEvent.getContextData().get("userEmail");
        String username = notificationEvent.getContextData().get("userName");
        log.info("Handling USER_REGISTERED event for: {} with email {}", username, email);
        emailService.sendWelcomeEmail(email, username);
    }
    @Override
    public boolean supports(String eventType) {
        return "USER_REGISTERED".equals(eventType);
    }
}
