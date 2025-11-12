package io.github.adrian.wieczorek.local_trade.repository;

import io.github.adrian.wieczorek.local_trade.model.ChatMessageEntity;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findBySenderAndRecipient(UsersEntity user1, UsersEntity user2);
}
