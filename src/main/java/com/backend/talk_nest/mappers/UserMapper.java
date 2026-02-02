package com.backend.talk_nest.mappers;

import com.backend.talk_nest.dtos.users.requests.CreateUserRequest;
import com.backend.talk_nest.dtos.users.responses.UserResponse;
import com.backend.talk_nest.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);

    @Mapping(target = "password", ignore = true)
    User toEntity(CreateUserRequest userRequest);
}
