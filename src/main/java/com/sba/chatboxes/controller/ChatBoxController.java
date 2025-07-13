package com.sba.chatboxes.controller;

import com.sba.chatboxes.dto.ChatMessageDTO;
import com.sba.chatboxes.dto.ChatSessionDTO;
import com.sba.chatboxes.service.ChatBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatboxes")
public class ChatBoxController {
    private final ChatBoxService chatBoxService;

    @PostMapping("/session")
    public ResponseEntity<ChatSessionDTO> createChatSession(
            @RequestBody ChatSessionDTO sessionDTO) {
        ChatSessionDTO savedSession = chatBoxService.createNewSession(sessionDTO);
        return ResponseEntity.ok(savedSession);
    }

    @GetMapping("/sessions/{userId}")
    public ResponseEntity<List<ChatSessionDTO>> getAllSessions(@PathVariable String userId) {
        return ResponseEntity.ok(chatBoxService.getAllSessionsByUserId(userId));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getSessionMessages(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatBoxService.getMessagesBySessionId(sessionId));
    }

    @PostMapping("/message")
    public ResponseEntity<ChatMessageDTO> processMessage(@RequestBody ChatMessageDTO messageDTO) {
        ChatMessageDTO result = chatBoxService.handleRestMessage(messageDTO).join();

        return ResponseEntity.accepted().body(result);
    }

    // WebSocket endpoint for message processing
    @MessageMapping("/chat.sendMessage")
    public void handleWebSocketMessage(@Payload ChatMessageDTO messageDTO) {
        chatBoxService.handleWebSocketMessage(messageDTO);
    }

    @DeleteMapping("/message/{sessionId}/cancel/{requestId}")
    public ResponseEntity<String> cancelProcessing(
            @PathVariable String sessionId,
            @PathVariable String requestId) {

        chatBoxService.handleMessageCancel(sessionId, requestId);
        return ResponseEntity.ok("Cancellation request sent");
    }
}