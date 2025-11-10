package io.github.czjena.notifications.service.infrastructure;

import io.github.czjena.dtos.NotificationEvent;

public interface NotificationHandler {
    void handle(NotificationEvent notificationEvent);
    boolean supports(String eventType);
}
