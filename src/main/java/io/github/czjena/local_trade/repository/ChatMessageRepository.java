package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.ChatMessage;
import io.github.czjena.local_trade.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndRecipient(Users user1, Users user2);
}
