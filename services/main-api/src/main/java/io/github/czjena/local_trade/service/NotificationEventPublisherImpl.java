package io.github.czjena.local_trade.service;

import io.github.czjena.dtos.NotificationEvent;
import io.github.czjena.local_trade.configs.RabbitMQConfig;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Users;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationEventPublisherImpl implements NotificationEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public NotificationEventPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;

    }
    @Override
    public void publishAndCreateEvent(Users user, Advertisement newAdvertisement) {
        Map<String, String> contextData = Map.of(
                "adId",newAdvertisement.getAdvertisementId().toString(),
                "adTitle",newAdvertisement.getTitle(),
                "userName",user.getName()
        );
        NotificationEvent event = new NotificationEvent(
                "AD_CREATED",
                user.getUserId().toString(),
                contextData
        );
        String routingKey = "notification.event.ad_created";
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                routingKey,
                event
        );

    }
    @Override
    public void publishEvent(NotificationEvent event, String routingKey) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                routingKey,
                event
        );
    }
}
