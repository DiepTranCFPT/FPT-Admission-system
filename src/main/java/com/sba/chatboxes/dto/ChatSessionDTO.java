package com.sba.chatboxes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionDTO {
    private String id;
    private String title;
    private String userId;
    private ZonedDateTime createdAt;
    private String firstMessage;
    private String lastMessage;
}
