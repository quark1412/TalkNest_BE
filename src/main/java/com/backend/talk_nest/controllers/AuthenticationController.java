package com.backend.talk_nest.controllers;

import com.backend.talk_nest.dtos.ApiResponse;
import com.backend.talk_nest.dtos.LoginRequest;
import com.backend.talk_nest.dtos.AuthenticationResponse;
import com.backend.talk_nest.dtos.LogoutRequest;
import com.backend.talk_nest.services.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        var result = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws JOSEException, ParseException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .message("Đăng xuất thành công")
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
