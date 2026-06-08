package com.example.b11ndboard.user.controller;

import com.example.b11ndboard.auth.dto.request.SignUpRequest;
import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        return ResponseEntity.ok(userService.signUp(request));
    }
}

