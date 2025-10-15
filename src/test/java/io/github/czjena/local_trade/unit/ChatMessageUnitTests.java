package io.github.czjena.local_trade.unit;

import io.github.czjena.local_trade.dto.ChatMessageDto;
import io.github.czjena.local_trade.dto.ChatMessagePayload;
import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.model.ChatMessage;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.ChatMessageRepository;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.ChatMessageService;
import io.github.czjena.local_trade.testutils.UserUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatMessageUnitTests {
    @Mock
    ChatMessageRepository chatMessageRepository;
    @InjectMocks
    ChatMessageService chatMessageService;
    @Mock
    UsersRepository usersRepository;

    @Test
    public void whenSearchingForRecipientAndSenderAndSendingMessage_thenReturnChatMessage() {
        Users user1 = UserUtils.createUserRoleUser();
        Users user2 = UserUtils.createUserRoleUser();
        user2.setEmail("Test2@test2.pl");

        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn(user1.getEmail());

        ChatMessagePayload payload = new ChatMessagePayload("Test message");

        when(usersRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user1));
        when(usersRepository.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(i -> i.getArgument(0));

        ChatMessageDto chatMessage = chatMessageService.createAndSaveMessageForPrivateUser(payload,principal,user2.getEmail());

        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());
        ChatMessage saved = captor.getValue();

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(payload.content(),saved.getContent());
        Assertions.assertEquals(user1,saved.getSender());
        Assertions.assertEquals(user2,saved.getRecipient());
        Assertions.assertNotNull(chatMessage);
        Assertions.assertEquals(payload.content(),chatMessage.getContent());
    }

    @Test
    public void whenSearchingForRecipientAndSenderAndSendingMessageSenderNotFound_thenReturnUserNotFound() {
        Users user1 = UserUtils.createUserRoleUser();
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn(user1.getEmail());
        ChatMessagePayload payload = new ChatMessagePayload("Test message");

        when(usersRepository.findByEmail(user1.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> chatMessageService.createAndSaveMessageForPrivateUser(payload,principal,user1.getEmail()));
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));

    }

    @Test
    public void whenSearchingForRecipientAndSenderAndSendingMessageRecipientNotFound_thenReturnUserNotFound() {
        Users user1 = UserUtils.createUserRoleUser();
        Users user2 = UserUtils.createUserRoleUser();
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn(user1.getUsername());
        ChatMessagePayload payload = new ChatMessagePayload("Test message");
        when(usersRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user1));
        when(usersRepository.findByEmail(user2.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> chatMessageService.createAndSaveMessageForPrivateUser(payload,principal,user2.getEmail()));
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }

    @Test
    public void whenPullingMessageHistory_thenReturnChatMessageHistory() {
        Users user1 = UserUtils.createUserRoleUser();
        Users user2 = UserUtils.createUserRoleUser();
        List<ChatMessage> chatHistory = new ArrayList<>();
        List<ChatMessage> chatHistory2 = new ArrayList<>();
        ChatMessage messageFromSender = ChatMessage.builder()
                .recipient(user1)
                .sender(user2)
                .content("Hey its me!")
                .timestamp(LocalDateTime.now().minusMinutes(5))
                .build();
        ChatMessage messageFromRecipient = ChatMessage.builder()
                .recipient(user2)
                .sender(user1)
                .content("Do you want my car ?")
                .timestamp(LocalDateTime.now().minusMinutes(4))
                .build();

        chatHistory.add(messageFromSender);
        chatHistory2.add(messageFromRecipient);
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user1.getUsername());
        when(usersRepository.findByName(userDetails.getUsername())).thenReturn(Optional.of(user1));
        when(usersRepository.findByName(user2.getName())).thenReturn(Optional.of(user2));

        when(chatMessageRepository.findBySenderAndRecipient(user1, user2)).thenReturn(chatHistory);
        when(chatMessageRepository.findBySenderAndRecipient(user2, user1)).thenReturn(chatHistory2);

        List<ChatMessage> result = chatMessageService.getChatHistory(userDetails,user2.getName());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Hey its me!",result.get(0).getContent());
        Assertions.assertEquals("Do you want my car ?",result.get(1).getContent());

    }

    @Test
    public void whenSearchingForRecipientAndSenderAndSendingMessageRecipientFound_thenReturnUserFound() {
        Users user1 = UserUtils.createUserRoleUser();
        Users user2 = UserUtils.createUserRoleUser();
        user2.setName("user2");

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user1.getUsername());

        when(usersRepository.findByName(userDetails.getUsername())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> chatMessageService.getChatHistory(userDetails,user2.getName()));
        verify(usersRepository, never()).findByName(user2.getName());
    }
}
