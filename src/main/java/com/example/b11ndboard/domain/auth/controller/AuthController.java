package com.example.b11ndboard.domain.auth.controller;

import com.example.b11ndboard.domain.auth.service.AuthService;
import com.example.b11ndboard.domain.auth.dto.request.LoginRequest;
import com.example.b11ndboard.domain.auth.dto.request.SignUpRequest;
import com.example.b11ndboard.global.security.MemberDetails;
import com.example.b11ndboard.global.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.refresh(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal MemberDetails memberDetails,
            HttpServletResponse response
    ) {

        return ResponseEntity.ok(authService.logout(memberDetails.getUserId(), response));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        return ResponseEntity.ok(authService.signUp(request));
    }
}