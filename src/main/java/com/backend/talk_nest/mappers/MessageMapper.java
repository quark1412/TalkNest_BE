package com.backend.talk_nest.mappers;

import com.backend.talk_nest.dtos.messages.SendMessageResponse;
import com.backend.talk_nest.entities.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "id", expression = "java(message.getId() != null ? message.getId().toString() : null)")
    @Mapping(target = "conversationId", expression = "java(message.getConversation() != null && message.getConversation().getId() != null ? message.getConversation().getId().toString() : null)")
    @Mapping(target = "senderId", expression = "java(message.getSender() != null && message.getSender().getId() != null ? message.getSender().getId().toString() : null)")
    SendMessageResponse toResponse(Message message);

    Message toEntity(SendMessageResponse response);
}
