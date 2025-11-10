package io.github.czjena.local_trade.service.infrastructure;

import io.github.czjena.dtos.NotificationEvent;

public interface NotificationEventPublisher {

    void publishEvent(NotificationEvent notificationEvent,String routingKey);
}
