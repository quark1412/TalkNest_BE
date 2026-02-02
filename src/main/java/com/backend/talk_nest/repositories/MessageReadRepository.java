package com.backend.talk_nest.repositories;

import com.backend.talk_nest.entities.MessageRead;
import com.backend.talk_nest.entities.ids.MessageReadId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageReadRepository extends JpaRepository<MessageRead, MessageReadId> {
    List<MessageRead> findByIdMessageId(UUID messageId);
    List<MessageRead> findByIdUserId(UUID userId);
}
