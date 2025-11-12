package io.github.czjena.local_trade.service.business;

import io.github.czjena.dtos.NotificationEvent;
import io.github.czjena.local_trade.configs.RabbitMQConfig;
import io.github.czjena.local_trade.service.infrastructure.NotificationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitNotificationEventPublisher implements NotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;



    @Override
    public void publishEvent(NotificationEvent event, String routingKey) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                routingKey,
                event
        );
    }
}
