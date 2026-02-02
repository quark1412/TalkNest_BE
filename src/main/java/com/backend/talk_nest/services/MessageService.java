package com.backend.talk_nest.services;

import com.backend.talk_nest.dtos.messages.SendMessageRequest;
import com.backend.talk_nest.dtos.messages.SendMessageResponse;
import com.backend.talk_nest.entities.Conversation;
import com.backend.talk_nest.entities.Member;
import com.backend.talk_nest.entities.Message;
import com.backend.talk_nest.entities.User;
import com.backend.talk_nest.exceptions.AppException;
import com.backend.talk_nest.mappers.MessageMapper;
import com.backend.talk_nest.repositories.ConversationRepository;
import com.backend.talk_nest.repositories.MemberRepository;
import com.backend.talk_nest.repositories.MessageRepository;
import com.backend.talk_nest.repositories.UserRepository;
import com.backend.talk_nest.utils.enums.ErrorCode;
import com.backend.talk_nest.utils.enums.MessageType;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public SendMessageResponse sendMessage(SendMessageRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

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
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UUID cId = UUID.fromString(conversationId);
        Conversation conversation = conversationRepository.findById(cId).orElseThrow(() -> new AppException(ErrorCode.INPUT_INVALID));

        // ensure current user is a member of conversation
        List<Member> members = memberRepository.findMemberByConversation_Id(cId);
        boolean isMember = members.stream().anyMatch(m -> Objects.equals(m.getId().getUserId(), currentUser.getId()));
        if (!isMember) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        List<Message> messages = messageRepository.findByConversation_IdOrderByCreatedAtAsc(cId);
        return messages.stream().map(messageMapper::toResponse).toList();
    }
}
