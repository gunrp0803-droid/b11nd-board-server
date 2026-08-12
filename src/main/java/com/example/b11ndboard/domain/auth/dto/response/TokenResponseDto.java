package com.example.b11ndboard.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDto {
    private final String accessToken;
    private final String refreshToken;
}
