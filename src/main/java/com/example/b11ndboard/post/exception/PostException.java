package com.example.b11ndboard.post.exception;

import com.example.b11ndboard.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class PostException extends RuntimeException {
    private final ErrorCode errorCode;

    public PostException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}