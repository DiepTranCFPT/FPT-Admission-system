package com.sba.chatboxes.repository;

import com.sba.chatboxes.pojos.ChatBoxSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatBoxSession, String> {
    List<ChatBoxSession> findByUser_IdOrderByCreatedAtDesc(String userId);
}
