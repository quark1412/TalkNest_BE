package com.backend.talk_nest.dtos.users.requests;

import com.backend.talk_nest.utils.validators.PasswordConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "USERNAME_MISSING")
    @Size(min = 3, message = "USERNAME_INVALID_LENGTH")
    private String username;

    @NotBlank(message = "EMAIL_MISSING")
    @Email(message =  "EMAIL_INVALID")
    private String email;

    @PasswordConstraint
    private String password;
}
