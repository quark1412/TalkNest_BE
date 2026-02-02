package com.backend.talk_nest.dtos.conversations.responses;

import com.backend.talk_nest.dtos.members.responses.MemberResponse;
import com.backend.talk_nest.dtos.messages.SendMessageResponse;
import com.backend.talk_nest.dtos.users.responses.UserResponse;
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
    private SendMessageResponse lastMessage;
    private UserResponse counterpart;

}
