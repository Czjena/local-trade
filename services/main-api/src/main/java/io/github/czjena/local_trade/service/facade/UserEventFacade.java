package io.github.czjena.local_trade.service.facade;

import io.github.czjena.dtos.NotificationEvent;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.service.infrastructure.NotificationEventPublisher;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class UserEventFacade {

    private final NotificationEventPublisher publisher;

    public UserEventFacade(NotificationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishUserRegistered(Users newUser) {
        Map<String, String> contextData = Map.of(
                "userName", newUser.getName(),
                "userEmail", newUser.getEmail()
        );

        NotificationEvent event = new NotificationEvent(
                "USER_REGISTERED",
                newUser.getUserId(),
                contextData
        );

        String routingKey = "notification.event.user_registered";

        publisher.publishEvent(event, routingKey);
    }
}