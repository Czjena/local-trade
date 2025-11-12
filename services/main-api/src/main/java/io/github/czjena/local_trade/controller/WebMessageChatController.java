package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.dto.ChatMessageDto;
import io.github.czjena.local_trade.dto.ChatMessagePayload;
import io.github.czjena.local_trade.model.ChatMessage;
import io.github.czjena.local_trade.service.infrastructure.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
public class WebMessageChatController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/chat.sendMessage.public")
    @SendTo("/topic/public")
    public ChatMessage sendPublicMessage(@Payload ChatMessage chatMessage) {
        chatMessageService.save(chatMessage);
        return chatMessage;
    }
    @MessageMapping("/chat.sendMessage.private/{recipient}")
    @Operation(summary = "Take logged in user and send recipient Username to send message")
    public void sendPrivateMessage(@Payload ChatMessagePayload chatMessage, @DestinationVariable("recipient") String recipient, @AuthenticationPrincipal Principal principal) {
        ChatMessageDto newChatMessage = chatMessageService.createAndSaveMessageForPrivateUser(chatMessage,principal,recipient);
        simpMessagingTemplate.convertAndSendToUser(recipient,"/queue/messages", newChatMessage);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/messages", newChatMessage);
    }
}