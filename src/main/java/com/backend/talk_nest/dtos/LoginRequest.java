package com.backend.talk_nest.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
