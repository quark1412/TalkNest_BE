package com.backend.talk_nest.controllers;

import com.backend.talk_nest.dtos.ApiResponse;
import com.backend.talk_nest.dtos.conversations.requests.CreateConversationRequest;
import com.backend.talk_nest.dtos.conversations.responses.ConversationResponse;
import com.backend.talk_nest.services.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping
    public ApiResponse<ConversationResponse> createConversation(@RequestBody CreateConversationRequest request) {
        var result = conversationService.createConversation(request);

        return ApiResponse.<ConversationResponse>builder()
                .data(result)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
