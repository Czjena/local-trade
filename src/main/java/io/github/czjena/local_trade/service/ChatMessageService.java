package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.ChatMessageDto;
import io.github.czjena.local_trade.dto.ChatMessagePayload;
import io.github.czjena.local_trade.model.ChatMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

public interface ChatMessageService {
    @Transactional
    ChatMessage save(ChatMessage chatMessage);

    @Transactional
    ChatMessageDto createAndSaveMessageForPrivateUser(ChatMessagePayload chatMessage, Principal principal, String recipientEmail);

    @Transactional(readOnly = true)
    List<ChatMessage> getChatHistory(UserDetails sender, String recipientUsername);
}
