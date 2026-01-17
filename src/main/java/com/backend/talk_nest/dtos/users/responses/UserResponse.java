package com.backend.talk_nest.dtos.users.responses;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
}
