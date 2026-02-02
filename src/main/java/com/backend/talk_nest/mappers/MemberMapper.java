package com.backend.talk_nest.mappers;

import com.backend.talk_nest.dtos.members.responses.MemberResponse;
import com.backend.talk_nest.entities.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    @Mapping(source = "id.conversationId", target = "conversationId")
    @Mapping(source = "id.userId", target = "userId")
    @Mapping(source = "isActive", target = "isActive")
    MemberResponse toResponse(Member member);

    Member toEntity(MemberResponse memberResponse);
}
