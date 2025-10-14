package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.ChatMessagePayload;
import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.model.ChatMessage;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.ChatMessageRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final UsersRepository usersRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, UsersRepository usersRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.usersRepository = usersRepository;
    }

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(chatMessage);
    }
    @Transactional
    public ChatMessage createAndSaveMessageForPrivateUser(ChatMessagePayload chatMessage, UserDetails userDetails, String recipientUsername) {
        Users user = usersRepository.findByName(userDetails.getUsername())
                .orElseThrow(() ->new UserNotFoundException("User not found with username :" + userDetails.getUsername()));
        Users user1 = usersRepository.findByName(recipientUsername)
                .orElseThrow(() ->new UserNotFoundException("User not found with username :" + recipientUsername ));
        ChatMessage newChatMessage = ChatMessage.builder()
                .sender(user)
                .recipient(user1)
                .content(chatMessage.content())
                .build();
     return chatMessageRepository.save(newChatMessage);
    }
    @Transactional
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
