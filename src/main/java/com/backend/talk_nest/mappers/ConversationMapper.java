package com.backend.talk_nest.mappers;

import com.backend.talk_nest.dtos.conversations.responses.ConversationResponse;
import com.backend.talk_nest.entities.Conversation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationResponse toResponse(Conversation conversation);

    Conversation toEntity(ConversationResponse conversationResponse);
}
