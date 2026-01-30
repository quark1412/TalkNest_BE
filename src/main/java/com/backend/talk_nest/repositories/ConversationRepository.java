package com.backend.talk_nest.repositories;

import com.backend.talk_nest.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("SELECT c FROM Conversation c " +
            "JOIN c.memberList m1 " +
            "JOIN c.memberList m2 " +
            "WHERE c.isGroup = false " +
            "AND m1.user.id = :userAId " +
            "AND m2.user.id = :userBId ")
    Optional<Conversation> findPrivateConversation(@Param("userAId") UUID userAId, @Param("userBId") UUID userBId);
}
