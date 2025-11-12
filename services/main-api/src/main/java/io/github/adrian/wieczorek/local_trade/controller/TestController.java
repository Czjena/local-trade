package io.github.adrian.wieczorek.local_trade.controller;


import io.github.adrian.wieczorek.dtos.NotificationEvent;
import io.github.adrian.wieczorek.local_trade.service.infrastructure.NotificationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    private final NotificationEventPublisher eventPublisher;



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
                    UUID.randomUUID(),
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

