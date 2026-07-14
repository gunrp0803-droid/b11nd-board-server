package com.example.b11ndboard.domain.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
