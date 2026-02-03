package com.backend.talk_nest.controllers;

import com.backend.talk_nest.dtos.ApiResponse;
import com.backend.talk_nest.dtos.friends.requests.SendFriendRequestRequest;
import com.backend.talk_nest.dtos.friends.responses.FriendRequestResponse;
import com.backend.talk_nest.services.FriendListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/friend-requests")
@RequiredArgsConstructor
public class FriendListController {
    private final FriendListService friendListService;

    @PostMapping
    public ApiResponse<Void> sendFriendRequest(@RequestBody SendFriendRequestRequest request) {
        friendListService.sendFriendRequest(request);
        return ApiResponse.<Void>builder()
                .message("Đã gửi lời mời kết bạn")
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<List<FriendRequestResponse>> getIncomingRequests(@PathVariable String userId) {
        var result = friendListService.getIncomingRequestsByUserId(userId);
        return ApiResponse.<List<FriendRequestResponse>>builder()
                .data(result)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @PostMapping("/{senderId}/approve")
    public ApiResponse<Void> approveRequest(@PathVariable String senderId) {
        friendListService.approveRequest(senderId);
        return ApiResponse.<Void>builder()
                .message("Đã chấp nhận lời mời kết bạn")
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @PostMapping("/{senderId}/reject")
    public ApiResponse<Void> rejectRequest(@PathVariable String senderId) {
        friendListService.rejectRequest(senderId);
        return ApiResponse.<Void>builder()
                .message("Đã từ chối lời mời kết bạn")
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
