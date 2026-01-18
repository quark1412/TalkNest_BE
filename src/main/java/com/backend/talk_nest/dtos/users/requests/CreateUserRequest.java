package com.backend.talk_nest.dtos.users.requests;

import com.backend.talk_nest.utils.validators.PasswordConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, message = "Username phải có ít nhất 3 kí tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message =  "Định dạng email không hợp lệ")
    private String email;

    @PasswordConstraint
    private String password;
}
