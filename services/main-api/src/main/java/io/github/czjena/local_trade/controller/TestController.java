package io.github.czjena.local_trade.controller;


import io.github.czjena.dtos.NotificationEvent;
import io.github.czjena.local_trade.configs.RabbitMQConfig;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.NotificationEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    private final NotificationEventPublisher eventPublisher;

    public TestController(NotificationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello ";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hello1")
    public String hello2() {
        return "hello2";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/hello2")
    public String hello3() {
        return "hello3";
    }

    @GetMapping("/test-notification")
    public String testNotification() {

        Map<String, String> fakeContext = Map.of(
                    "adId", "test-ad-id-123",
                    "adTitle", "Testing advert",
                    "userName", "Adrian"
            );

            NotificationEvent event = new NotificationEvent(
                    "AD_CREATED",
                    "test-user-id-abc",
                    fakeContext
            );

            String routingKey = "notification.event.ad_created";


            try {
                eventPublisher.publishEvent(event, routingKey);
            } catch (Exception e) {
                return "Błąd przy wysyłaniu: " + e.getMessage();
            }
            return "OK! Zdarzenie testowe wysłane do RabbitMQ. Sprawdź logi 'notification-service'!";
        }
}

