package com.backend.talk_nest.controllers;

import com.backend.talk_nest.dtos.ApiResponse;
import com.backend.talk_nest.dtos.users.requests.CreateUserRequest;
import com.backend.talk_nest.dtos.users.responses.UserResponse;
import com.backend.talk_nest.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request,
            UriComponentsBuilder uriBuilder) {
        UserResponse data = userService.createUser(request);
        var uri = uriBuilder.path("/user/{id}").buildAndExpand(data.getId()).toUri();
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .data(data)
                .timestamp(OffsetDateTime.now())
                .build();
        return ResponseEntity.created(uri).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> userResponseList = userService.getAllUsers();
        ApiResponse<List<UserResponse>> apiResponse = ApiResponse.<List<UserResponse>>builder()
                .data(userResponseList)
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        UserResponse userResponse = userService.getUserById(id);
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .data(userResponse)
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
