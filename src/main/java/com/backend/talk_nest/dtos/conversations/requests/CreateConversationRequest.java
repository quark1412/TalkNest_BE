package com.backend.talk_nest.dtos.conversations.requests;

import lombok.Data;

import java.util.List;

@Data
public class CreateConversationRequest {
    private List<String> participantIds;
    private String title;
    private String type;
}
