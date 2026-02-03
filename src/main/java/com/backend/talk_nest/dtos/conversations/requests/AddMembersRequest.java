package com.backend.talk_nest.dtos.conversations.requests;

import lombok.Data;

import java.util.List;

@Data
public class AddMembersRequest {
    private List<String> userIds;
}
