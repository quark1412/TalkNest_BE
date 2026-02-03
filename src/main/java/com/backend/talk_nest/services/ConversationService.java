package com.backend.talk_nest.services;

import com.backend.talk_nest.dtos.conversations.requests.AddMembersRequest;
import com.backend.talk_nest.dtos.conversations.requests.ChangeMemberRoleRequest;
import com.backend.talk_nest.dtos.conversations.requests.CreateConversationRequest;
import com.backend.talk_nest.dtos.conversations.requests.UpdateConversationRequest;
import com.backend.talk_nest.dtos.conversations.responses.ConversationResponse;
import com.backend.talk_nest.dtos.messages.SendMessageResponse;
import com.backend.talk_nest.dtos.members.responses.MemberResponse;
import com.backend.talk_nest.dtos.users.responses.UserResponse;
import com.backend.talk_nest.entities.Conversation;
import com.backend.talk_nest.entities.Member;
import com.backend.talk_nest.entities.User;
import com.backend.talk_nest.entities.ids.MemberId;
import com.backend.talk_nest.exceptions.AppException;
import com.backend.talk_nest.mappers.ConversationMapper;
import com.backend.talk_nest.mappers.MemberMapper;
import com.backend.talk_nest.mappers.MessageMapper;
import com.backend.talk_nest.mappers.UserMapper;
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
    private final MemberMapper memberMapper;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

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
                    .isActive(false)
                    .lastActive(OffsetDateTime.now())
                    .user(user)
                    .role(request.getType().equals(ConversationType.GROUP.toString()) && userId.equals(currentUser.getId().toString()) ? MemberRole.ADMIN : MemberRole.MEMBER)
                    .joinedAt(OffsetDateTime.now())
                    .build();
            memberRepository.save(member);
        }
        return conversationMapper.toResponse(conversation);
    }

    public List<ConversationResponse> getConversationsForCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Conversation> conversations = conversationRepository.findAllByMemberUserIdWithLastMessage(currentUser.getId());

        List<ConversationResponse> responses = new ArrayList<>();
        for (Conversation c : conversations) {
            var resp = conversationMapper.toResponse(c);
            if (c.getLastMessage() != null) {
                resp.setLastMessage(messageMapper.toResponse(c.getLastMessage()));
            }
            if (!Boolean.TRUE.equals(c.getIsGroup())) {
                for (Member m : c.getMemberList()) {
                    if (!m.getUser().getId().equals(currentUser.getId())) {
                        resp.setCounterpart(userMapper.toResponse(m.getUser()));
                        break;
                    }
                }
            }
            responses.add(resp);
        }

        return responses;
    }

    @Transactional
    public void leaveConversation(String conversationId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UUID cId = UUID.fromString(conversationId);
        MemberId memberId = new MemberId(cId, currentUser.getId());
        memberRepository.deleteById(memberId);
    }

    @Transactional
    public ConversationResponse updateConversation(String conversationId, UpdateConversationRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UUID cId = UUID.fromString(conversationId);
        Conversation conversation = conversationRepository.findById(cId).orElseThrow(() -> new AppException(ErrorCode.INPUT_INVALID));

        if (!Boolean.TRUE.equals(conversation.getIsGroup())) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }

        MemberId currentMemberId = new MemberId(cId, currentUser.getId());
        Member currentMember = memberRepository.findById(currentMemberId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!MemberRole.ADMIN.equals(currentMember.getRole())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (request.getTitle() != null) {
            conversation.setTitle(request.getTitle());
        }
        if (request.getAvatarUrl() != null) {
            conversation.setAvatarUrl(request.getAvatarUrl());
        }
        conversation.setUpdatedBy(currentUser);
        conversationRepository.save(conversation);

        return conversationMapper.toResponse(conversation);
    }

    @Transactional
    public void addMembers(String conversationId, AddMembersRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UUID cId = UUID.fromString(conversationId);
        Conversation conversation = conversationRepository.findById(cId).orElseThrow(() -> new AppException(ErrorCode.INPUT_INVALID));

        if (!Boolean.TRUE.equals(conversation.getIsGroup())) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }

        MemberId currentMemberId = new MemberId(cId, currentUser.getId());
        Member currentMember = memberRepository.findById(currentMemberId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!MemberRole.ADMIN.equals(currentMember.getRole())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        for (String userIdStr : request.getUserIds()) {
            UUID userId = UUID.fromString(userIdStr);
            User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            MemberId memberId = new MemberId(cId, user.getId());
            if (memberRepository.existsById(memberId)) {
                continue;
            }

            Member member = Member.builder()
                    .id(memberId)
                    .conversation(conversation)
                    .isActive(true)
                    .lastActive(OffsetDateTime.now())
                    .user(user)
                    .role(MemberRole.MEMBER)
                    .joinedAt(OffsetDateTime.now())
                    .build();
            memberRepository.save(member);
        }
    }

    @Transactional
    public void removeMember(String conversationId, String userIdStr) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UUID cId = UUID.fromString(conversationId);
        UUID targetUserId = UUID.fromString(userIdStr);

        Conversation conversation = conversationRepository.findById(cId).orElseThrow(() -> new AppException(ErrorCode.INPUT_INVALID));

        MemberId currentMemberId = new MemberId(cId, currentUser.getId());
        Member currentMember = memberRepository.findById(currentMemberId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!currentUser.getId().equals(targetUserId) && !MemberRole.ADMIN.equals(currentMember.getRole())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        MemberId targetMemberId = new MemberId(cId, targetUserId);
        if (!memberRepository.existsById(targetMemberId)) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }

        memberRepository.deleteById(targetMemberId);
    }

    @Transactional
    public void changeMemberRole(String conversationId, String userIdStr, ChangeMemberRoleRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UUID cId = UUID.fromString(conversationId);
        UUID targetUserId = UUID.fromString(userIdStr);

        Conversation conversation = conversationRepository.findById(cId).orElseThrow(() -> new AppException(ErrorCode.INPUT_INVALID));

        if (!Boolean.TRUE.equals(conversation.getIsGroup())) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }

        MemberId currentMemberId = new MemberId(cId, currentUser.getId());
        Member currentMember = memberRepository.findById(currentMemberId).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!MemberRole.ADMIN.equals(currentMember.getRole())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        MemberId targetMemberId = new MemberId(cId, targetUserId);
        Member targetMember = memberRepository.findById(targetMemberId).orElseThrow(() -> new AppException(ErrorCode.INPUT_INVALID));

        try {
            MemberRole newRole = MemberRole.valueOf(request.getRole());
            targetMember.setRole(newRole);
            memberRepository.save(targetMember);
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }
    }
}
