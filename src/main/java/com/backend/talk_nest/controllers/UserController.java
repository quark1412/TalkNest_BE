package com.backend.talk_nest.controllers;

import com.backend.talk_nest.dtos.users.requests.CreateUserRequest;
import com.backend.talk_nest.dtos.users.responses.UserResponse;
import com.backend.talk_nest.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody CreateUserRequest request,
            UriComponentsBuilder uriBuilder) {
        UserResponse response = userService.createUser(request);
        var uri = uriBuilder.path("/user/${id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }
}
