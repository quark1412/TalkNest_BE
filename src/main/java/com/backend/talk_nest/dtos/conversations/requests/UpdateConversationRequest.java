package com.backend.talk_nest.dtos.conversations.requests;

import lombok.Data;

@Data
public class UpdateConversationRequest {
    private String title;
    private String avatarUrl;
}
