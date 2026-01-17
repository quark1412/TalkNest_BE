package com.backend.talk_nest.services;

import com.backend.talk_nest.dtos.users.requests.CreateUserRequest;
import com.backend.talk_nest.dtos.users.responses.UserResponse;
import com.backend.talk_nest.mappers.UserMapper;
import com.backend.talk_nest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(CreateUserRequest request) {
        var user = userMapper.toEntity(request);

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        var savedUser =  userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
