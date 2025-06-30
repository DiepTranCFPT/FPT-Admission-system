package com.sba.chatboxes.service;

import com.sba.chatboxes.dto.ChatMessageDTO;
import com.sba.chatboxes.dto.ChatSessionDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChatBoxService {
    void handleWebSocketMessage(ChatMessageDTO messageDTO);

    void handleMessageCancel(String sessionId, String requestId);

    CompletableFuture<ChatMessageDTO> handleRestMessage(ChatMessageDTO messageDTO);

    ChatSessionDTO createNewSession(ChatSessionDTO sessionDTO);

    List<ChatSessionDTO> getAllSessions();

    List<ChatMessageDTO> getMessagesBySessionId(String sessionId);
}