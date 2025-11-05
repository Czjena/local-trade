package io.github.czjena.local_trade.integration;

import io.github.czjena.local_trade.dto.ChatMessageDto;
import io.github.czjena.local_trade.dto.ChatMessagePayload;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.ChatMessageRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.ChatMessageService;
import io.github.czjena.local_trade.service.JwtService;
import io.github.czjena.local_trade.service.TestJwtUtils;
import io.github.czjena.local_trade.testutils.UserUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import resources.AbstractIntegrationTest;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "security.jwt.secret-key=41c6701ad7f5abf1db2b053a2f1a39ad41189e00462ec987622b5409dbc0006d")
@Testcontainers
@AutoConfigureMockMvc
public class WebChatIntegrationTests extends AbstractIntegrationTest {

    @Autowired
    JwtService jwtService;
    @Autowired
    UsersRepository usersRepository;

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private String url;
    private String senderJwt;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private ChatMessageRepository chatMessageRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @BeforeEach
    public void setup() {
        this.url = "ws://localhost:" + port + "/ws/websocket";
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        Users sender = UserUtils.createUserRoleUser();
        Users receiver = UserUtils.createUserRoleUser();
        sender.setEmail("Tomek@wp.pl");
        sender.setName("Tomek");
        receiver.setName("Ania");
        receiver.setEmail("Ania@wp.pl");
        receiver.setUserId(UUID.randomUUID());
        usersRepository.saveAndFlush(sender);
        usersRepository.saveAndFlush(receiver);


        this.senderJwt = TestJwtUtils.generateToken(jwtService, sender);
    }

    @Test
    public void sendChatMessage() throws Exception {
        final BlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(1);
        ChatMessagePayload payload = new ChatMessagePayload("Hey how are you?");

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + senderJwt);

        StompSessionHandlerAdapter sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/user/queue/messages", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ChatMessageDto.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        blockingQueue.offer(payload);
                    }
                });
                session.send("/app/chat.sendMessage.private/Ania@wp.pl", payload);
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                System.err.println("--- ZŁAPANO BŁĄD STOMP ---");
                exception.printStackTrace();
                blockingQueue.offer(exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println("--- ZŁAPANO BŁĄD TRANSPORTOWY ---");
                exception.printStackTrace();
                blockingQueue.offer(exception);
            }
        };

        CompletableFuture<StompSession> future = stompClient.connectAsync(
                url, new WebSocketHttpHeaders(), connectHeaders, sessionHandler);
        future.get(5, TimeUnit.SECONDS);

        Object result = blockingQueue.poll(5, TimeUnit.SECONDS);

        Assertions.assertNotNull(result, "Nie otrzymano niczego (wiadomości ani błędu) w ciągu 5 sekund.");

        if (result instanceof Throwable) {
            Assertions.fail("Test zakończony z powodu błędu przechwyconego przez handler", (Throwable) result);
        }

        Assertions.assertInstanceOf(ChatMessageDto.class, result, "Otrzymany obiekt nie jest błędem, ale nie jest też typu ChatMessageDto.");

        ChatMessageDto chatMessage = (ChatMessageDto) result;
        Assertions.assertEquals("Hey how are you?", chatMessage.getContent());
        Assertions.assertEquals("Tomek", chatMessage.getSender());
        Assertions.assertEquals("Ania", chatMessage.getRecipient());
        chatMessageRepository.deleteAll();
    }
}

