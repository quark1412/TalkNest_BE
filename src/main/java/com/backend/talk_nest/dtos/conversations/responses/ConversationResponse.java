package com.backend.talk_nest.dtos.conversations.responses;

import com.backend.talk_nest.dtos.members.responses.MemberResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationResponse {
    private String id;
    private String title;
    private Boolean isGroup;
    private List<MemberResponse> memberList;

}
