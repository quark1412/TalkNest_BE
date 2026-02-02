package com.backend.talk_nest.controllers;

import com.backend.talk_nest.dtos.ApiResponse;
import com.backend.talk_nest.dtos.members.responses.MemberResponse;
import com.backend.talk_nest.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/{conversationId}")
    public ApiResponse<List<MemberResponse>> getAllMembersByConversationId(@PathVariable String conversationId) {
        var result = memberService.getAllMembersByConversationId(conversationId);

        return ApiResponse.<List<MemberResponse>>builder()
                .data(result)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
