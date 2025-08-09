package com.example.chatbot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document("chat_messages")
public class ChatMessage {
    public enum Sender {
        USER, AI
    }

    @Id
    private String id;
    private String username;
    private String text;
    private Sender sender;
    private LocalDateTime timestamp;

    public ChatMessage(String username, String text, Sender sender) {
        this.username = username;
        this.text = text;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }
}