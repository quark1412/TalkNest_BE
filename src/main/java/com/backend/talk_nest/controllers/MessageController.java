package com.backend.talk_nest.controllers;

import com.backend.talk_nest.dtos.ApiResponse;
import com.backend.talk_nest.dtos.messages.SendMessageRequest;
import com.backend.talk_nest.dtos.messages.SendMessageResponse;
import com.backend.talk_nest.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ApiResponse<SendMessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
        var result = messageService.sendMessage(request);

        return ApiResponse.<SendMessageResponse>builder()
                .data(result)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @GetMapping
    public ApiResponse<List<SendMessageResponse>> getMessagesByConversationId(@RequestParam String conversationId) {
        var result = messageService.getMessagesByConversationId(conversationId);
        return ApiResponse.<List<SendMessageResponse>>builder()
                .data(result)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
