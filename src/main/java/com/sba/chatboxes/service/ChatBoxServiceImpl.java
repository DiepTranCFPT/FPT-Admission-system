package com.sba.chatboxes.service;

import com.sba.authentications.repositories.AuthenticationRepository;
import com.sba.chatboxes.dto.ChatMessageDTO;
import com.sba.chatboxes.dto.ChatSessionDTO;
import com.sba.chatboxes.pojos.ChatBoxMessage;
import com.sba.chatboxes.pojos.ChatBoxSession;
import com.sba.chatboxes.repository.ChatMessageRepository;
import com.sba.chatboxes.repository.ChatSessionRepository;
import com.sba.configs.RabbitMQConfig;
import com.sba.enums.Roles;
import com.sba.exceptions.AuthException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatBoxServiceImpl implements ChatBoxService {
    private final AuthenticationRepository authenticationRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate websocketTemplate;
    private final Map<String, Boolean> activeTasks = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(ChatBoxServiceImpl.class);
    private final Jackson2JsonMessageConverter messageConverter;

    @Override
    public CompletableFuture<ChatMessageDTO> handleRestMessage(ChatMessageDTO messageDTO) {
        // Generate a request ID if not provided
        if (messageDTO.getRequestId() == null) {
            messageDTO.setRequestId(UUID.randomUUID().toString());
        }

        handleUserMessage(messageDTO);

        // Return immediate acknowledgment
        return CompletableFuture.completedFuture(
                ChatMessageDTO.builder()
                        .sessionId(messageDTO.getSessionId())
                        .requestId(messageDTO.getRequestId())
                        .content("Message received, processing...")
                        .status("PROCESSING")
                        .build()
        );
    }

    @Override
    public void handleWebSocketMessage(ChatMessageDTO messageDTO) {
        handleUserMessage(messageDTO);
    }

    // Step 1: Handle user message, save to DB, forward to Python
    public void handleUserMessage(ChatMessageDTO messageDTO) {
        String requestId = messageDTO.getRequestId();
        String sessionId = messageDTO.getSessionId();

        try {
            // Save user message to DB
            ChatBoxSession session = chatSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Chat session not found"));

            if (chatMessageRepository.countAllByChatBoxSession_Id(sessionId) == 0) {
                // If this is the first message, request title generation
                String titleRequestId = UUID.randomUUID().toString();
                Map<String, Object> titleRequest = Map.of(
                        "sessionId", sessionId,
                        "content", messageDTO.getContent(),
                        "requestId", titleRequestId
                );

                logger.info("Requesting title generation for new session: {}", sessionId);
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.PYTHON_EXCHANGE,
                        "generate-title",
                        titleRequest,
                        message -> {
                            message.getMessageProperties().setReplyTo(rabbitTemplate.getDefaultReceiveQueue());
                            message.getMessageProperties().setType("title-response");
                            return message;
                        }
                );
            }

            ChatBoxMessage savedMessage = createChatMessage(session, Roles.USER, messageDTO.getContent());

            // Mark task as active
            activeTasks.put(requestId, true);

            // Send confirmation via WebSocket
            ChatMessageDTO savedDTO = convertMessageToDTO(savedMessage);
            savedDTO.setRequestId(requestId);
            savedDTO.setStatus("RECEIVED");
            websocketTemplate.convertAndSend("/topic/chat/" + sessionId, savedDTO);

            // Check if already cancelled
            if (!activeTasks.getOrDefault(requestId, false)) {
                logger.info("Task already cancelled: {}", requestId);
                return;
            }

            // Forward to Python service
            logger.info("Forwarding message to Python service: {}", requestId);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PYTHON_EXCHANGE,
                    "message-request",
                    messageDTO,
                    message -> {
                        message.getMessageProperties().setReplyTo(rabbitTemplate.getDefaultReceiveQueue());
                        message.getMessageProperties().setType("chat-response");
                        return message;
                    }
            );

            // Send processing update
            websocketTemplate.convertAndSend(
                    "/topic/chat/" + sessionId,
                    ChatMessageDTO.builder()
                            .sessionId(sessionId)
                            .requestId(requestId)
                            .content("Processing your request...")
                            .role(Roles.BOT)
                            .status("PROCESSING")
                            .build()
            );
        } catch (Exception e) {
            logger.error("Error handling user message", e);

            // Send error notification
            websocketTemplate.convertAndSend(
                    "/topic/chat/" + sessionId,
                    ChatMessageDTO.builder()
                            .sessionId(sessionId)
                            .requestId(requestId)
                            .content("Error processing your message: " + e.getMessage())
                            .role(Roles.SYSTEM)
                            .status("ERROR")
                            .build()
            );
        }
    }

    @RabbitListener(queues = "#{replyQueue.name}")
    public void handleResponse(Message message) {
        String type = message.getMessageProperties().getType();
        logger.info("Received message with type: {}", type);

        try {
            Object payload = messageConverter.fromMessage(message);

            if (type == null) {
                logger.warn("Message received without type property");
                handleMapAsMessageResponse(payload);
                return;
            }

            switch (type) {
                case "chat-response":
                    handleMapAsMessageResponse(payload);
                    break;
                case "title-response":
                    @SuppressWarnings("unchecked")
                    Map<String, Object> response = (Map<String, Object>) payload;
                    handleTitleResponse(response);
                    break;
                default:
                    logger.warn("Unknown message type: {}", type);
            }
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
        }
    }

    private void handleMapAsMessageResponse(Object payload) {
        if (payload instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> messageMap = (Map<String, Object>) payload;
            ChatMessageDTO messageDTO = ChatMessageDTO.builder()
                    .id((String) messageMap.get("id"))
                    .sessionId((String) messageMap.get("sessionId"))
                    .content((String) messageMap.get("content"))
                    .role(messageMap.get("role") != null ?
                            Roles.valueOf(messageMap.get("role").toString()) : null)
                    .requestId((String) messageMap.get("requestId"))
                    .status((String) messageMap.get("status"))
                    .createdAt((ZonedDateTime) messageMap.get("createdAt"))
                    .build();
            handleMessageResponse(messageDTO);
        } else if (payload instanceof ChatMessageDTO) {
            handleMessageResponse((ChatMessageDTO) payload);
        } else {
            logger.error("Unexpected payload type: {}", payload.getClass());
        }
    }

    // Step 2: Handle bot response from Python, save to DB, send to client

//    @RabbitListener(queues = RabbitMQConfig.PYTHON_MESSAGE_RESPONSE_QUEUE)
//    @RabbitListener(queues = "#{replyQueue.name}")
    public void handleMessageResponse(ChatMessageDTO messageDTO) {
        String requestId = messageDTO.getRequestId();
        String sessionId = messageDTO.getSessionId();

        try {
            // Check if task is still active
            if (!activeTasks.getOrDefault(requestId, false)) {
                logger.info("Task was cancelled, not saving response: {}", requestId);
                return;
            }

            // Save bot message to DB
            ChatBoxSession session = chatSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Chat session not found"));

            ChatBoxMessage botMessage = createChatMessage(session, Roles.BOT, messageDTO.getContent());

            // Send response to client
            ChatMessageDTO responseDTO = convertMessageToDTO(botMessage);
            responseDTO.setRequestId(requestId);
            responseDTO.setStatus("COMPLETED");

            websocketTemplate.convertAndSend(
                    "/topic/chat/" + sessionId,
                    responseDTO
            );
        } catch (Exception e) {
            logger.error("Error handling bot response", e);

            // Send error notification
            websocketTemplate.convertAndSend(
                    "/topic/chat/" + sessionId,
                    ChatMessageDTO.builder()
                            .sessionId(sessionId)
                            .requestId(requestId)
                            .content("Error processing bot response: " + e.getMessage())
                            .role(Roles.SYSTEM)
                            .status("ERROR")
                            .build()
            );
        } finally {
            // Remove from active tasks
            activeTasks.remove(requestId);
        }
    }

//    @RabbitListener(queues = RabbitMQConfig.PYTHON_TITLE_RESPONSE_QUEUE)
//    @RabbitListener(queues = "#{titleReplyQueue.name}")
    public void handleTitleResponse(Map<String, Object> response) {
        if ("ERROR".equals(response.get("status"))) {
            logger.error("Failed to generate title for session {}: {}", response.get("sessionId"), response.get("error"));
            return;
        }
        String sessionId = (String) response.get("sessionId");
        String generatedTitle = (String) response.get("title");

        logger.info("Received generated title '{}' for session: {}", generatedTitle, sessionId);

        try {
            chatSessionRepository.findById(sessionId).ifPresent(session -> {
                session.setTitle(generatedTitle);
                ChatBoxSession updatedSession = chatSessionRepository.save(session);

                // Notify clients about the updated session with new title
                websocketTemplate.convertAndSend(
                        "/topic/sessions",
                        convertSessionToDTO(updatedSession)
                );
            });
            logger.info("Sent generated title '{}' for session: {}", generatedTitle, sessionId);
        } catch (Exception e) {
            logger.error("Error updating session title", e);
        }
    }

    @Override
    public void handleMessageCancel(String sessionId, String requestId) {
        // Create cancellation event
        Map<String, String> cancelEvent = Map.of(
                "sessionId", sessionId,
                "requestId", requestId
        );

        // Remove from active tasks
        activeTasks.remove(requestId);

        // Send cancellation to Python service
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PYTHON_EXCHANGE,
                "cancel",
                cancelEvent
        );

        // Notify client about cancellation
        websocketTemplate.convertAndSend(
                "/topic/chat/" + sessionId,
                ChatMessageDTO.builder()
                        .sessionId(sessionId)
                        .requestId(requestId)
                        .content("Request was cancelled")
                        .role(Roles.SYSTEM)
                        .status("CANCELLED")
                        .build()
        );
    }

    @Override
    public ChatSessionDTO createNewSession(ChatSessionDTO sessionDTO) {
        ChatBoxSession session = createChatSession(sessionDTO);
        ChatSessionDTO savedSession = ChatSessionDTO.builder()
                .id(session.getId())
                .title(session.getTitle())
                .createdAt(session.getCreatedAt())
                .build();

        websocketTemplate.convertAndSend("/topic/sessions", savedSession);
        return savedSession;
    }

    @Override
    public List<ChatSessionDTO> getAllSessionsByUserId(String userId) {
        return chatSessionRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream().map(this::convertSessionToDTO).toList();
    }

    @Override
    public List<ChatMessageDTO> getMessagesBySessionId(String sessionId) {
        return chatMessageRepository.findChatBoxMessageByChatBoxSession_Id(sessionId)
                .stream()
                .map(this::convertMessageToDTO)
                .sorted(Comparator.comparing(ChatMessageDTO::getCreatedAt))
                .toList();
    }

    private ChatBoxMessage createChatMessage(ChatBoxSession session, Roles role, String content) {
        ChatBoxMessage message = new ChatBoxMessage();
        message.setChatBoxSession(session);
        message.setRole(role);
        message.setContent(content);
        message.setStatus("RECEIVED");

        return chatMessageRepository.save(message);
    }

    private ChatBoxSession createChatSession(ChatSessionDTO sessionDTO) {
        ChatBoxSession session = new ChatBoxSession();

        session.setUser(authenticationRepository.findById(sessionDTO.getUserId()).orElseThrow(() -> new AuthException("User not found")));
        session.setTitle(sessionDTO.getTitle() != null ? sessionDTO.getTitle() : "New Chat");
        ChatBoxSession savedSession = chatSessionRepository.save(session);

        // If there's a first message, request title generation
        if (sessionDTO.getFirstMessage() != null && !sessionDTO.getFirstMessage().trim().isEmpty()) {
            String requestId = UUID.randomUUID().toString();

            Map<String, Object> titleRequest = Map.of(
                    "sessionId", savedSession.getId(),
                    "content", sessionDTO.getFirstMessage(),
                    "requestId", requestId
            );

            logger.info("Requesting title generation for new session: {}", savedSession.getId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PYTHON_EXCHANGE,
                    "generate-title",
                    titleRequest,
                    message -> {
                        message.getMessageProperties().setReplyTo(rabbitTemplate.getDefaultReceiveQueue());
                        message.getMessageProperties().setType("title-response");
                        return message;
                    }
            );
        }

        return savedSession;
    }

    private ChatSessionDTO convertSessionToDTO(ChatBoxSession session) {
        return ChatSessionDTO.builder()
                .id(session.getId())
                .title(session.getTitle())
                .createdAt(session.getCreatedAt() != null ? session.getCreatedAt() : null)
                .build();
    }

    private ChatMessageDTO convertMessageToDTO(ChatBoxMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .sessionId(message.getChatBoxSession().getId())
                .content(message.getContent())
                .role(message.getRole())
                .createdAt(message.getCreatedAt() != null ? message.getCreatedAt() : null)
                .build();
    }
}