package com.example.b11ndboard.auth.service;

import com.example.b11ndboard.auth.entity.Role;
import com.example.b11ndboard.auth.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtProvider jwtProvider;

    public void generateTokens(String username, Role role, HttpServletResponse response) {
        String accessToken = jwtProvider.generateAccessToken(username, role);
        String refreshToken = jwtProvider.generateRefreshToken(username, role);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .httpOnly(false)
                .maxAge(Duration.ofMillis(jwtProvider.getAccessExpiration()))
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .httpOnly(false)
                .maxAge(Duration.ofMillis(jwtProvider.getRefreshExpiration()))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    public void generateAccessToken(String username, Role role, HttpServletResponse response) {
        String accessToken = jwtProvider.generateAccessToken(username, role);

        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .httpOnly(false)
                .maxAge(Duration.ofMillis(jwtProvider.getAccessExpiration()))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}