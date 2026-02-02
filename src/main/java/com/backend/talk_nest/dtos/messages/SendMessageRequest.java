package com.backend.talk_nest.dtos.messages;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String conversationId;
    private String content;
    private String mediaUrl;
    private String messageType;
}
