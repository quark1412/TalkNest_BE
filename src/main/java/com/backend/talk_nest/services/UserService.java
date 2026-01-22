package com.backend.talk_nest.services;

import com.backend.talk_nest.dtos.users.requests.CreateUserRequest;
import com.backend.talk_nest.dtos.users.responses.UserResponse;
import com.backend.talk_nest.exceptions.AppException;
import com.backend.talk_nest.mappers.UserMapper;
import com.backend.talk_nest.repositories.UserRepository;
import com.backend.talk_nest.utils.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXIST);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXIST);
        }

        var user = userMapper.toEntity(request);

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        var savedUser =  userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        var userList = userRepository.findAll();

        return userList.stream().map(userMapper::toResponse).toList();
    }

    public UserResponse getUserById(String id) {
        var user = userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }
}
