package com.backend.talk_nest.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
}
