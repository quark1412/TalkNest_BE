package com.backend.talk_nest.mappers;

import com.backend.talk_nest.dtos.members.responses.MemberResponse;
import com.backend.talk_nest.entities.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    @Mapping(target = "conversationId", expression = "java(member.getId() != null && member.getId().getConversationId() != null ? member.getId().getConversationId().toString() : null)")
    @Mapping(target = "userId", expression = "java(member.getId() != null && member.getId().getUserId() != null ? member.getId().getUserId().toString() : null)")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "isActive", target = "isActive")
    MemberResponse toResponse(Member member);

    Member toEntity(MemberResponse memberResponse);
}
