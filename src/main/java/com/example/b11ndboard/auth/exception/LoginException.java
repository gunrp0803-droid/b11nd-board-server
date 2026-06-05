package com.example.b11ndboard.auth.exception;

import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {

    private final ErrorCode errorCode;

    public LoginException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

