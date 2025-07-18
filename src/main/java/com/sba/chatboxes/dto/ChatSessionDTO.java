package com.sba.chatboxes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionDTO {
    private String id;
    private String title;
    private String userId;
    private LocalDateTime createdAt;
    private String firstMessage;
    private String lastMessage;
}
