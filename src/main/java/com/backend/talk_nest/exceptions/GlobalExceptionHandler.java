package com.backend.talk_nest.exceptions;

import com.backend.talk_nest.dtos.ApiResponse;
import com.backend.talk_nest.utils.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleSystemException(RuntimeException exception) {
        ApiResponse apiResponse = ApiResponse.builder()
                .code(ErrorCode.SYSTEM_ERROR.getCode())
                .message(ErrorCode.SYSTEM_ERROR.getMessage())
                .timestamp(OffsetDateTime.now())
                .build();
        return ResponseEntity.status(500).body(apiResponse);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse =
                ApiResponse.builder()
                        .message(errorCode.getMessage())
                        .code(errorCode.getCode())
                        .timestamp(OffsetDateTime.now())
                        .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException exception) {
        ErrorCode errorCode = ErrorCode.INPUT_INVALID;

        try {
            errorCode = ErrorCode.valueOf(Objects.requireNonNull(exception.getFieldError()).getDefaultMessage());
        } catch (IllegalArgumentException e){
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .timestamp(OffsetDateTime.now())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }
}
