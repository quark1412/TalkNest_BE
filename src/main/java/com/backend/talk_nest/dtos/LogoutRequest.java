package com.backend.talk_nest.dtos;

import lombok.Data;

@Data
public class LogoutRequest {
    private String accessToken;
}
