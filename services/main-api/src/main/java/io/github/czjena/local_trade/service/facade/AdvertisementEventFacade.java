package io.github.czjena.local_trade.service.facade;

import io.github.czjena.dtos.NotificationEvent;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.service.infrastructure.NotificationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdvertisementEventFacade {

    private final NotificationEventPublisher publisher;

    public void publishAdCreated(Users user, Advertisement newAdvertisement) {
        Map<String, String> contextData = Map.of(
                "adId", newAdvertisement.getAdvertisementId().toString(),
                "adTitle", newAdvertisement.getTitle(),
                "userName", user.getName(),
                "userEmail", user.getEmail()
        );

        NotificationEvent event = new NotificationEvent(
                "AD_CREATED",
                user.getUserId(),
                contextData
        );

        String routingKey = "notification.event.ad_created";

        publisher.publishEvent(event, routingKey);
    }

    public void publishAdUpdated(Users editor, Advertisement updatedAdvertisement) {
        Map<String, String> contextData = Map.of(
                "adId", updatedAdvertisement.getAdvertisementId().toString(),
                "adTitle", updatedAdvertisement.getTitle(),
                "editorName", editor.getName()
        );

        NotificationEvent event = new NotificationEvent(
                "AD_UPDATED",
                editor.getUserId(),
                contextData
        );

        String routingKey = "notification.event.ad_updated";

        publisher.publishEvent(event, routingKey);
    }
}