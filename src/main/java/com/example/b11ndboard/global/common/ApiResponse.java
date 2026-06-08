package com.example.b11ndboard.global.common;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final ResponseKind kind;
    private final T data;

    private ApiResponse(boolean success, String message, ResponseKind kind, T data) {
        this.success = success;
        this.message = message;
        this.kind = kind;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(ResponseKind kind, T data) {
        return new ApiResponse<>(true,"성공", kind, data);
    }

    public static <T> ApiResponse<T> ok(String message, ResponseKind kind, T data) {
        return new ApiResponse<>(true, message, kind, data);
    }

    public static <T> ApiResponse<T> fail(String message, ResponseKind kind) {
        return new ApiResponse<>(false, message, kind, null);
    }
}
