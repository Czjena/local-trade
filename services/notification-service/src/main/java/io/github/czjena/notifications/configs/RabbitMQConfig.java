package io.github.czjena.notifications.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "notification.exchange";

    public static final String QUEUE_NAME = "notification.queue";

    public static final String ROUTING_KEY_PATTERN = "notification.event.#";

    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE_NAME,true);
    }
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(EXCHANGE_NAME,true,false);

    }
    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(notificationExchange)
                .with(ROUTING_KEY_PATTERN);
    }
}
