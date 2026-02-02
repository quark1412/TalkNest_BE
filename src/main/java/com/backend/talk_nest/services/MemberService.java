package com.backend.talk_nest.services;

import com.backend.talk_nest.dtos.members.responses.MemberResponse;
import com.backend.talk_nest.mappers.MemberMapper;
import com.backend.talk_nest.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public List<MemberResponse> getAllMembersByConversationId(String conversationId) {
        var result = memberRepository.findMemberByConversation_Id(UUID.fromString(conversationId));

        return result.stream().map(memberMapper::toResponse).toList();
    }
}
