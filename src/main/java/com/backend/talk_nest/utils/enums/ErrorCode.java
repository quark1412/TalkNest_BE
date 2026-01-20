package com.backend.talk_nest.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter @AllArgsConstructor
public enum ErrorCode {

    // Errors related to System
    SYSTEM_ERROR(0, "Có lỗi xảy ra từ hệ thống", HttpStatus.INTERNAL_SERVER_ERROR),

    // Errors related to Validation
    INPUT_INVALID(1, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    USERNAME_MISSING(2, "Username không được để trống", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID_LENGTH(3, "Username phải có ít nhất 3 kí tự", HttpStatus.BAD_REQUEST),
    EMAIL_MISSING(4, "Email không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(5, "Định dạng email không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(5, "Mật khẩu cần ít nhất 8 kí tự, chứa ít nhất 1 số và 1 kí tự đặc biệt", HttpStatus.BAD_REQUEST),

    // Errors related to User
    USERNAME_ALREADY_EXIST(100, "Username đã được sử dụng", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXIST(101, "Email đã được sử dụng", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(102, "Người dùng không tồn tại", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
