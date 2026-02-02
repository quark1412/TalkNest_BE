package com.backend.talk_nest.controllers;

import com.backend.talk_nest.dtos.ApiResponse;
import com.backend.talk_nest.dtos.messages.SendMessageRequest;
import com.backend.talk_nest.dtos.messages.SendMessageResponse;
import com.backend.talk_nest.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{conversationId}")
    public ApiResponse<Page<SendMessageResponse>> getMessagesByConversationIdPaginated(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = messageService.getMessagesByConversationIdPaginated(conversationId, page, size);
        return ApiResponse.<Page<SendMessageResponse>>builder()
                .data(result)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @DeleteMapping("/{messageId}")
    public ApiResponse<Void> deleteMessage(@PathVariable String messageId) {
        messageService.deleteMessage(messageId);
        return ApiResponse.<Void>builder()
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @PostMapping("/{messageId}/read")
    public ApiResponse<Void> markMessageRead(@PathVariable String messageId) {
        messageService.markMessageRead(messageId);
        return ApiResponse.<Void>builder()
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
