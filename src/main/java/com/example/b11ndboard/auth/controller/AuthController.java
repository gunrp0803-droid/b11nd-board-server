package com.example.b11ndboard.auth.controller;

import com.example.b11ndboard.auth.security.MemberDetails;
import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.auth.dto.request.LoginRequest;
import com.example.b11ndboard.auth.service.AuthService;
import com.example.b11ndboard.global.common.ResponseKind;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
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

}