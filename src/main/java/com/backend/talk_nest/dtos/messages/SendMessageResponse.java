package com.backend.talk_nest.dtos.messages;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class SendMessageResponse {
    private String id;
    private String conversationId;
    private String senderId;
    private String content;
    private String mediaUrl;
    private String messageType;
    private OffsetDateTime createdAt;
}
