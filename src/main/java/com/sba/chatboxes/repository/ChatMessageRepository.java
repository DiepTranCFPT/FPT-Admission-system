package com.sba.chatboxes.repository;

import com.sba.chatboxes.pojos.ChatBoxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatBoxMessage, String> {
    List<ChatBoxMessage> findChatBoxMessageByChatBoxSession_Id(String id);
    Long countAllByChatBoxSession_Id(String id);
}
