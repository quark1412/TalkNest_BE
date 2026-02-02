package com.backend.talk_nest.entities;

import com.backend.talk_nest.entities.ids.MessageReadId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "message_reads")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MessageRead {
    @EmbeddedId
    private MessageReadId id;

    @CreationTimestamp
    @Column(name = "read_at")
    private OffsetDateTime readAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageId")
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}
