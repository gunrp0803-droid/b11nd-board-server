package com.example.b11ndboard.auth.service;

import com.example.b11ndboard.auth.entity.Role;
import com.example.b11ndboard.auth.jwt.JwtProvider;
import com.example.b11ndboard.auth.repository.UsersRepository;
import jakarta.servlet.http.Cookie;
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

    public String generateAccessToken(String username , Role role, HttpServletResponse response) {
        String accessToken = jwtProvider.generateAccessToken(username, role);

        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .httpOnly(false)
                .maxAge(Duration.ofHours(1))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return accessToken;
    }
}