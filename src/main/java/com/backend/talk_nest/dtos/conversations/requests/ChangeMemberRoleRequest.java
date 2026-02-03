package com.backend.talk_nest.dtos.conversations.requests;

import lombok.Data;

@Data
public class ChangeMemberRoleRequest {
    private String role;
}
