package io.github.czjena.local_trade.integration;

import io.github.czjena.dtos.NotificationEvent;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.repository.CategoryRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.facade.AdvertisementEventFacade;
import io.github.czjena.local_trade.service.infrastructure.NotificationEventPublisher;
import io.github.czjena.local_trade.testutils.AdUtils;
import io.github.czjena.local_trade.testutils.CategoryUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import resources.AbstractIntegrationTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "security.jwt.secret-key=41c6701ad7f5abf1db2b053a2f1a39ad41189e00462ec987622b5409dbc0006d")
@Testcontainers
@AutoConfigureMockMvc
public class NotificationFlowIntegrationTests extends AbstractIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AdvertisementRepository advertisementRepository;


    private Users randomUser;
    private Advertisement randomAdvertisement;

    @Autowired
    private AdvertisementEventFacade advertisementEventFacade;
    @Autowired
    private UsersRepository usersRepository;
    @MockitoBean
    private NotificationEventPublisher notificationEventPublisher;

    @BeforeEach
    public void setup() {
        randomUser = UserUtils.createUserRoleUserUnitTestWithUUID();
        usersRepository.save(randomUser);
        Category category = CategoryUtils.createCategoryForIntegrationTests();
        categoryRepository.save(category);
        randomAdvertisement = AdUtils.createAdvertisementRoleUserForIntegrationTests(category, randomUser);
        advertisementRepository.save(randomAdvertisement);
    }

    @Test
    @Transactional
    public void happyPath_whenSendingEventToRightHandler_thenReturnOk() {
        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);

        advertisementEventFacade.publishAdCreated(randomUser, randomAdvertisement);

        verify(notificationEventPublisher, times(1))
                .publishEvent(eventCaptor.capture(), routingKeyCaptor.capture());

        // Sprawdź, CZYM został wywołany
        NotificationEvent capturedEvent = eventCaptor.getValue();
        assertEquals("AD_CREATED", capturedEvent.getEventType());
        assertEquals(randomUser.getUserId(), capturedEvent.getRecipientUserId());
        assertEquals("notification.event.ad_created", routingKeyCaptor.getValue());
    }
}
