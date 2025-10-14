package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.dto.ChatMessagePayload;
import io.github.czjena.local_trade.model.ChatMessage;
import io.github.czjena.local_trade.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;


@Controller
public class WebMessageChatController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebMessageChatController(ChatMessageService chatMessageService,SimpMessagingTemplate simpMessagingTemplate) {
        this.chatMessageService = chatMessageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat.sendMessage.public")
    @SendTo("/topic/public")
    public ChatMessage sendPublicMessage(@Payload ChatMessage chatMessage) {
        chatMessageService.save(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.sendMessage.private/{recipient}")
    @Operation(summary = "Take logged in user and send recipientUsername to send message")
    public void sendPrivateMessage(@Payload ChatMessagePayload chatMessage, @DestinationVariable String recipientUsername, @AuthenticationPrincipal UserDetails userDetails) {
        ChatMessage newChatMessage = chatMessageService.createAndSaveMessageForPrivateUser(chatMessage,userDetails,recipientUsername);
        simpMessagingTemplate.convertAndSendToUser(recipientUsername,"/queue/messages", newChatMessage);
    }
}
