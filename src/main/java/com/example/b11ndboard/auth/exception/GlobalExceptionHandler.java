package com.example.b11ndboard.auth.exception;

import com.example.b11ndboard.auth.common.ApiResponse;
import com.example.b11ndboard.auth.common.ResponseKind;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidException(
            MethodArgumentNotValidException e
    ) {
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message, ResponseKind.VALIDATION_ERROR));
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ApiResponse<Void>> handleLoginException(
            LoginException e
    ) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode.getMessage(), ResponseKind.LOGIN));
    }

    @ExceptionHandler(SignUpException.class)
    public ResponseEntity<ApiResponse<Void>> handleSignUpException(
            SignUpException e
    ) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode.getMessage(), ResponseKind.SIGNUP));
    }
}