package com.backend.talk_nest.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter @AllArgsConstructor
public enum ErrorCode {

    // Errors related to User
    USERNAME_ALREADY_EXIST("USERNAME_ALREADY_EXIST", "Username đã được sử dụng", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXIST("EMAIL_ALREADY_EXIST", "Email đã được sử dụng", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("USER_NOT_FOUND", "Người dùng không tồn tại", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatusCode statusCode;
}
