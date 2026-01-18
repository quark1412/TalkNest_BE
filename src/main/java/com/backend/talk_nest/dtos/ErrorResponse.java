package com.backend.talk_nest.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private String code;
    private LocalDateTime timestamp;
}
