package io.github.czjena.notifications.service;

import io.github.czjena.dtos.NotificationEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public interface NotificationEventListener {
    @RabbitListener(queues = "notification.queue")
    void handleNotificationEvent(NotificationEvent event);
}
