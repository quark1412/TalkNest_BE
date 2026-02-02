package com.backend.talk_nest.services;

import com.backend.talk_nest.dtos.messages.SendMessageRequest;
import com.backend.talk_nest.dtos.messages.SendMessageResponse;
import com.backend.talk_nest.entities.Conversation;
import com.backend.talk_nest.entities.Member;
import com.backend.talk_nest.entities.Message;
import com.backend.talk_nest.entities.MessageRead;
import com.backend.talk_nest.entities.User;
import com.backend.talk_nest.entities.ids.MessageReadId;
import com.backend.talk_nest.exceptions.AppException;
import com.backend.talk_nest.mappers.MessageMapper;
import com.backend.talk_nest.repositories.ConversationRepository;
import com.backend.talk_nest.repositories.MemberRepository;
import com.backend.talk_nest.repositories.MessageReadRepository;
import com.backend.talk_nest.repositories.MessageRepository;
import com.backend.talk_nest.repositories.UserRepository;
import com.backend.talk_nest.utils.enums.ErrorCode;
import com.backend.talk_nest.utils.enums.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final MemberRepository memberRepository;
    private final MessageReadRepository messageReadRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public SendMessageResponse sendMessage(SendMessageRequest request) {
        User currentUser = getCurrentUser();

        Conversation conversation = conversationRepository.findById(UUID.fromString(request.getConversationId())).orElseThrow(() -> new AppException(ErrorCode.INPUT_INVALID));

        // Build message
        MessageType type = MessageType.TEXT;
        try {
            if (request.getMessageType() != null) {
                type = MessageType.valueOf(request.getMessageType());
            }
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }

        Message message = Message.builder()
                .conversation(conversation)
                .sender(currentUser)
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .messageType(type)
                .createdAt(OffsetDateTime.now())
                .build();

        Message saved = messageRepository.save(message);

        // update conversation lastMessage
        conversation.setLastMessage(saved);
        conversationRepository.save(conversation);

        return messageMapper.toResponse(saved);
    }

    public List<SendMessageResponse> getMessagesByConversationId(String conversationId) {
        User currentUser = getCurrentUser();

        UUID cId = UUID.fromString(conversationId);
        if (!conversationRepository.existsById(cId)) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }

        // ensure current user is a member of conversation
        List<Member> members = memberRepository.findMemberByConversation_Id(cId);
        boolean isMember = members.stream().anyMatch(m -> Objects.equals(m.getId().getUserId(), currentUser.getId()));
        if (!isMember) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        List<Message> messages = messageRepository.findByConversation_IdOrderByCreatedAtAsc(cId);
        return messages.stream().map(messageMapper::toResponse).toList();
    }

    public Page<SendMessageResponse> getMessagesByConversationIdPaginated(String conversationId, int page, int size) {
        User currentUser = getCurrentUser();

        UUID cId = UUID.fromString(conversationId);
        if (!conversationRepository.existsById(cId)) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }

        List<Member> members = memberRepository.findMemberByConversation_Id(cId);
        boolean isMember = members.stream().anyMatch(m -> Objects.equals(m.getId().getUserId(), currentUser.getId()));
        if (!isMember) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagesPage = messageRepository.findByConversation_IdOrderByCreatedAtAsc(cId, pageable);
        return messagesPage.map(messageMapper::toResponse);
    }

    @Transactional
    public void deleteMessage(String messageId) {
        User currentUser = getCurrentUser();

        UUID mId = UUID.fromString(messageId);
        Message message = messageRepository.findById(mId).orElseThrow(() -> new AppException(ErrorCode.INPUT_INVALID));

        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        message.setIsDeleted(true);
        messageRepository.save(message);
    }

    @Transactional
    public void markMessageRead(String messageId) {
        User currentUser = getCurrentUser();

        UUID mId = UUID.fromString(messageId);
        Message message = messageRepository.findById(mId).orElseThrow(() -> new AppException(ErrorCode.INPUT_INVALID));

        UUID cId = message.getConversation().getId();
        List<Member> members = memberRepository.findMemberByConversation_Id(cId);
        boolean isMember = members.stream().anyMatch(m -> Objects.equals(m.getId().getUserId(), currentUser.getId()));
        if (!isMember) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        MessageReadId id = new MessageReadId(mId, currentUser.getId());
        if (messageReadRepository.existsById(id)) {
            return;
        }

        MessageRead mr = MessageRead.builder()
                .id(id)
                .message(message)
                .user(currentUser)
                .build();
        messageReadRepository.save(mr);
    }
}
