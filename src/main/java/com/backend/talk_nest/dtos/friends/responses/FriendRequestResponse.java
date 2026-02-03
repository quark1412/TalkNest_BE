package com.backend.talk_nest.dtos.friends.responses;

import com.backend.talk_nest.dtos.users.responses.UserResponse;
import com.backend.talk_nest.utils.enums.FriendStatus;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class FriendRequestResponse {
    private UserResponse requester;
    private FriendStatus status;
    private OffsetDateTime createdAt;
}
