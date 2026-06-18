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
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재 하지않는 게시글"),
    NOT_POST_AUTHOR(HttpStatus.BAD_REQUEST, "게시글의 작성자가 아닙니다."),
    POST_UPDATE_FORBIDDEN(HttpStatus.BAD_REQUEST, "게시글 수정 권한이 없습니다."),
    POST_DELETE_FORBIDDEN(HttpStatus.BAD_REQUEST, "게시글 삭제 권한이 없습니다."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시글 입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 기록을 찾을 수 없습니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),
    COMMENT_UPDATE_FORBIDDEN(HttpStatus.BAD_REQUEST, "댓글 수정 권한이 없습니다."),
    COMMENT_DELETE_FORBIDDEN(HttpStatus.BAD_REQUEST, "댓글 삭제 권한이 없습니다.");

    @Getter
    private final HttpStatus status;
    @Getter
    private final String message;
}