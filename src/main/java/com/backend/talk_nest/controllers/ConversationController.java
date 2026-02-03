package com.backend.talk_nest.controllers;

import com.backend.talk_nest.dtos.ApiResponse;
import com.backend.talk_nest.dtos.conversations.requests.CreateConversationRequest;
import com.backend.talk_nest.dtos.conversations.requests.UpdateConversationRequest;
import com.backend.talk_nest.dtos.conversations.requests.AddMembersRequest;
import com.backend.talk_nest.dtos.conversations.requests.ChangeMemberRoleRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;

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

    @GetMapping("/{id}")
    public ApiResponse<ConversationResponse> getConversationById(@PathVariable String id) {
        var result = conversationService.getConversationById(id);
        return ApiResponse.<ConversationResponse>builder()
                .data(result)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @PatchMapping("/{id}/leave")
    public ApiResponse<Void> leaveConversation(@PathVariable String id) {
        conversationService.leaveConversation(id);
        return ApiResponse.<Void>builder()
                .message("Rời cuộc trò chuyện thành công")
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @PatchMapping("/{id}")
    public ApiResponse<ConversationResponse> updateConversation(@PathVariable String id, @RequestBody UpdateConversationRequest request) {
        var result = conversationService.updateConversation(id, request);
        return ApiResponse.<ConversationResponse>builder()
                .data(result)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @PostMapping("/{id}/members")
    public ApiResponse<Void> addMembers(@PathVariable String id, @RequestBody AddMembersRequest request) {
        conversationService.addMembers(id, request);
        return ApiResponse.<Void>builder()
                .message("Thêm thành viên thành công")
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ApiResponse<Void> removeMember(@PathVariable String id, @PathVariable String userId) {
        conversationService.removeMember(id, userId);
        return ApiResponse.<Void>builder()
                .message("Xóa thành viên thành công")
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @PatchMapping("/{id}/members/{userId}/role")
    public ApiResponse<Void> changeMemberRole(@PathVariable String id, @PathVariable String userId, @RequestBody ChangeMemberRoleRequest request) {
        conversationService.changeMemberRole(id, userId, request);
        return ApiResponse.<Void>builder()
                .message("Cập nhật vai trò thành viên thành công")
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
