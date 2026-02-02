package com.backend.talk_nest.controllers;

import com.backend.talk_nest.dtos.ApiResponse;
import com.backend.talk_nest.dtos.conversations.requests.CreateConversationRequest;
import com.backend.talk_nest.dtos.conversations.responses.ConversationResponse;
import com.backend.talk_nest.services.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.OffsetDateTime;
import java.util.List;

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
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @GetMapping
    public ApiResponse<List<ConversationResponse>> getConversationsForCurrentUser() {
        var result = conversationService.getConversationsForCurrentUser();
        return ApiResponse.<List<ConversationResponse>>builder()
                .data(result)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @PatchMapping("/{id}/leave")
    public ApiResponse<Void> leaveConversation(@PathVariable String id) {
        conversationService.leaveConversation(id);
        return ApiResponse.<Void>builder()
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
