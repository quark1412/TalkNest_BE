package com.backend.talk_nest.repositories;

import com.backend.talk_nest.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversation_IdOrderByCreatedAtAsc(UUID conversationId);
    Page<Message> findByConversation_IdOrderByCreatedAtAsc(UUID conversationId, Pageable pageable);
}
