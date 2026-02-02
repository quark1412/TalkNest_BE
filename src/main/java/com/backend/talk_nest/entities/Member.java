package com.backend.talk_nest.entities;

import com.backend.talk_nest.entities.ids.MemberId;
import com.backend.talk_nest.utils.enums.MemberRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "members")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Member {
    @EmbeddedId
    private MemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conversationId")
    @JsonIgnore
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role = MemberRole.MEMBER;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_active")
    private OffsetDateTime lastActive;

    @Column(name = "joined_at")
    private OffsetDateTime joinedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;


}
