package com.backend.talk_nest.repositories;

import com.backend.talk_nest.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
}
