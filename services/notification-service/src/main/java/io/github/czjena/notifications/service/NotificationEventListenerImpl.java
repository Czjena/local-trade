package io.github.czjena.notifications.service;

import io.github.czjena.dtos.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationEventListenerImpl implements NotificationEventListener {


    @Override
    @RabbitListener(queues = "notification.queue")
    public void handleNotificationEvent (NotificationEvent event){
        log.info("Received notification event type: {}", event.getEventType());
        log.info("User UUID : {}", event.getRecipientUserId());
        log.info("Context data: {}", event.getContextData());
    }
}
