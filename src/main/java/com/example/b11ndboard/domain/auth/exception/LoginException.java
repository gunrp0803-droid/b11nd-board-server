package com.example.b11ndboard.domain.auth.exception;

import com.example.b11ndboard.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {

    private final ErrorCode errorCode;

    public LoginException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

