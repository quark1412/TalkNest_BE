package com.backend.talk_nest.exceptions;

import com.backend.talk_nest.utils.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class AppException extends RuntimeException{
    private final ErrorCode errorCode;
}
