package com.backend.talk_nest.services;

import com.backend.talk_nest.dtos.conversations.requests.CreateConversationRequest;
import com.backend.talk_nest.dtos.conversations.responses.ConversationResponse;
import com.backend.talk_nest.entities.Conversation;
import com.backend.talk_nest.entities.Member;
import com.backend.talk_nest.entities.User;
import com.backend.talk_nest.entities.ids.MemberId;
import com.backend.talk_nest.exceptions.AppException;
import com.backend.talk_nest.mappers.ConversationMapper;
import com.backend.talk_nest.repositories.ConversationRepository;
import com.backend.talk_nest.repositories.MemberRepository;
import com.backend.talk_nest.repositories.UserRepository;
import com.backend.talk_nest.utils.enums.ConversationType;
import com.backend.talk_nest.utils.enums.ErrorCode;
import com.backend.talk_nest.utils.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;
    private final MemberRepository memberRepository;

    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (Objects.equals(request.getType(), ConversationType.PRIVATE.toString())) {
            if (request.getParticipantIds().size() == 1) {
                UUID userAId = UUID.fromString(request.getParticipantIds().getFirst());
                var existingConversation = conversationRepository.findPrivateConversation(userAId, currentUser.getId());
                if (existingConversation.isPresent()) {
                    return conversationMapper.toResponse(existingConversation.get());
                }
            }
        }

        Conversation conversation = Conversation.builder()
                .isGroup(request.getType().equals(ConversationType.GROUP.toString()))
                .createdAt(OffsetDateTime.now())
                .createdBy(currentUser)
                .title(request.getTitle())
                .build();
        conversationRepository.save(conversation);

        List<String> participants = new ArrayList<>(request.getParticipantIds());
        participants.add(currentUser.getId().toString());

        for (String userId : participants) {
            User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            MemberId memberId = new MemberId(conversation.getId(), user.getId());

            Member member = Member.builder()
                    .id(memberId)
                    .conversation(conversation)
                    .user(user)
                    .role(request.getType().equals(ConversationType.GROUP.toString()) && userId.equals(currentUser.getId().toString()) ? MemberRole.ADMIN : MemberRole.MEMBER)
                    .joinedAt(OffsetDateTime.now())
                    .build();
            memberRepository.save(member);
        }
        return conversationMapper.toResponse(conversation);
    }
}
