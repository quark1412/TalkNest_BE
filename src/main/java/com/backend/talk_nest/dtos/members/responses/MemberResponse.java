package com.backend.talk_nest.dtos.members.responses;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MemberResponse {
    private String conversationId;
    private String userId;
    private String role;
    private Boolean isActive;
    private OffsetDateTime lastActive;
    private OffsetDateTime joinedAt;
}
