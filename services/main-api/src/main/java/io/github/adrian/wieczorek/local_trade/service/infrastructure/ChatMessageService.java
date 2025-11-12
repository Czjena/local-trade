package io.github.adrian.wieczorek.local_trade.service.infrastructure;

import io.github.adrian.wieczorek.local_trade.dto.ChatMessageDto;
import io.github.adrian.wieczorek.local_trade.dto.ChatMessagePayload;
import io.github.adrian.wieczorek.local_trade.model.ChatMessageEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

public interface ChatMessageService {
    @Transactional
    ChatMessageEntity save(ChatMessageEntity chatMessageEntity);

    @Transactional
    ChatMessageDto createAndSaveMessageForPrivateUser(ChatMessagePayload chatMessage, Principal principal, String recipientEmail);

    @Transactional(readOnly = true)
    List<ChatMessageEntity> getChatHistory(UserDetails sender, String recipientUsername);
}
