package com.sba.chatboxes.dto;

import com.sba.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private String id;
    private Roles role;
    private String content;
    private String sessionId;
    private String createdAt;
    private String requestId;
    private String status;
}
