package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.ChatMessageDto;
import io.github.czjena.local_trade.dto.ChatMessagePayload;
import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.model.ChatMessage;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.ChatMessageRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final UsersRepository usersRepository;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository, UsersRepository usersRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(chatMessage);
    }
    @Transactional
    @Override
    public ChatMessageDto createAndSaveMessageForPrivateUser(ChatMessagePayload chatMessage, Principal principal, String recipientEmail) {
        Users user = usersRepository.findByEmail(principal.getName())
                .orElseThrow(() ->new UserNotFoundException("User not found with username :" + principal.getName()));
        Users user1 = usersRepository.findByEmail(recipientEmail)
                .orElseThrow(() ->new UserNotFoundException("User not found with username :" + recipientEmail ));
        ChatMessage newChatMessage = ChatMessage.builder()
                .sender(user)
                .recipient(user1)
                .content(chatMessage.content())
                .build();
        ChatMessageDto dto = new ChatMessageDto(newChatMessage);
      chatMessageRepository.save(newChatMessage);
      return dto;
    }

    @Transactional
    @Override
    public List<ChatMessage> getChatHistory(UserDetails sender, String recipientUsername) {
        Users user1 = usersRepository.findByName(sender.getUsername())
                .orElseThrow(() ->new UserNotFoundException("User not found with username :" + sender.getUsername()));
        Users user2 = usersRepository.findByName(recipientUsername)
                .orElseThrow(() ->new UserNotFoundException("User not found with username :" + recipientUsername ));
        List<ChatMessage> history1 = chatMessageRepository.findBySenderAndRecipient(user1, user2);
        List<ChatMessage> history2 = chatMessageRepository.findBySenderAndRecipient(user2, user1);
        List<ChatMessage> fullHistory = new ArrayList<>();
        fullHistory.addAll(history1);
        fullHistory.addAll(history2);
        fullHistory.sort(Comparator.comparing(ChatMessage::getTimestamp));
        return fullHistory;
    }
}
