package io.github.czjena.local_trade.dto;

import io.github.czjena.local_trade.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private String content;
    private String sender;
    private String recipient;
    private LocalDateTime timestamp;

    public ChatMessageDto(ChatMessage entity) {
        this.content = entity.getContent();
        this.sender = entity.getSender().getName();
        this.recipient = entity.getRecipient().getName();
        this.timestamp = entity.getTimestamp();
    }
}

