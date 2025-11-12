package io.github.adrian.wieczorek.local_trade.service.business;

import io.github.adrian.wieczorek.local_trade.dto.ChatMessageDto;
import io.github.adrian.wieczorek.local_trade.dto.ChatMessagePayload;
import io.github.adrian.wieczorek.local_trade.exceptions.UserNotFoundException;
import io.github.adrian.wieczorek.local_trade.model.ChatMessageEntity;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import io.github.adrian.wieczorek.local_trade.repository.ChatMessageRepository;
import io.github.adrian.wieczorek.local_trade.repository.UsersRepository;
import io.github.adrian.wieczorek.local_trade.service.infrastructure.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final UsersRepository usersRepository;


    @Override
    @Transactional
    public ChatMessageEntity save(ChatMessageEntity chatMessageEntity) {
        chatMessageEntity.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(chatMessageEntity);
    }
    @Transactional
    @Override
    public ChatMessageDto createAndSaveMessageForPrivateUser(ChatMessagePayload chatMessage, Principal principal, String recipientEmail) {
        UsersEntity user = usersRepository.findByEmail(principal.getName())
                .orElseThrow(() ->new UserNotFoundException("User not found with username :" + principal.getName()));
        UsersEntity user1 = usersRepository.findByEmail(recipientEmail)
                .orElseThrow(() ->new UserNotFoundException("User not found with username :" + recipientEmail ));
        ChatMessageEntity newChatMessageEntity = ChatMessageEntity.builder()
                .sender(user)
                .recipient(user1)
                .content(chatMessage.content())
                .build();
        ChatMessageDto dto = new ChatMessageDto(newChatMessageEntity);
      chatMessageRepository.save(newChatMessageEntity);
      return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatMessageEntity> getChatHistory(UserDetails sender, String recipientUsername) {
        UsersEntity user1 = usersRepository.findByName(sender.getUsername())
                .orElseThrow(() ->new UserNotFoundException("User not found with username :" + sender.getUsername()));
        UsersEntity user2 = usersRepository.findByName(recipientUsername)
                .orElseThrow(() ->new UserNotFoundException("User not found with username :" + recipientUsername ));
        List<ChatMessageEntity> history1 = chatMessageRepository.findBySenderAndRecipient(user1, user2);
        List<ChatMessageEntity> history2 = chatMessageRepository.findBySenderAndRecipient(user2, user1);
        List<ChatMessageEntity> fullHistory = new ArrayList<>();
        fullHistory.addAll(history1);
        fullHistory.addAll(history2);
        fullHistory.sort(Comparator.comparing(ChatMessageEntity::getTimestamp));
        return fullHistory;
    }
}
