package com.example.b11ndboard.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@RequiredArgsConstructor
public enum ErrorCode {

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다."),
    SIGNUP_USERNAME_EMPTY(HttpStatus.BAD_REQUEST, "아이디를 입력해주세요."),
    SIGNUP_PASSWORD_EMPTY(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요."),
    SIGNUP_EMAIL_EMPTY(HttpStatus.BAD_REQUEST, "이메일을 입력해주세요."),
    SIGNUP_USERNAME_USED(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    SIGNUP_EMAIL_USED(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    NOT_POST_AUTHOR(HttpStatus.FORBIDDEN, "게시글의 작성자가 아닙니다."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시글입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요가 없습니다.");

    @Getter
    private final HttpStatus status;
    @Getter
    private final String message;
}