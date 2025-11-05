package io.github.czjena.local_trade.service;

import io.github.czjena.dtos.NotificationEvent;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Users;
import org.springframework.context.event.EventListener;

import java.util.UUID;

public interface NotificationEventPublisher {

    void publishAndCreateEvent(Users user, Advertisement advertisement);
    void publishEvent(NotificationEvent notificationEvent,String routingKey);
}
