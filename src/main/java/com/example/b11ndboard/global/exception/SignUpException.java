package com.example.b11ndboard.global.exception;

import lombok.Getter;

@Getter
public class SignUpException extends RuntimeException {

    private final ErrorCode errorCode;

    public SignUpException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}