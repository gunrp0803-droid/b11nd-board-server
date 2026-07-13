package com.example.b11ndboard.domain.auth.service;

import com.example.b11ndboard.domain.auth.dto.response.TokenResponse;
import com.example.b11ndboard.domain.auth.entity.RefreshToken;
import com.example.b11ndboard.domain.auth.repository.RefreshTokenRepository;
import com.example.b11ndboard.domain.user.entity.Role;
import com.example.b11ndboard.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenResponse generateTokens(String username, Role role, HttpServletResponse response) {
        // 기존의 해당 유저 리프레시 토큰이 있다면 무효화(삭제)하여 1유저 1토큰 세션 보장
        refreshTokenRepository.deleteByUsername(username);

        String accessToken = jwtProvider.generateAccessToken(username, role);
        String refreshToken = jwtProvider.generateRefreshToken(username, role);

        // 새로운 리프레시 토큰 DB 저장
        RefreshToken tokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .username(username)
                .build();
        refreshTokenRepository.save(tokenEntity);

        ResponseCookie accessCookie = createCookie("accessToken", accessToken, Duration.ofMillis(jwtProvider.getAccessExpiration()));
        ResponseCookie refreshCookie = createCookie("refreshToken", refreshToken, Duration.ofMillis(jwtProvider.getRefreshExpiration()));

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public void deleteTokens(String username, HttpServletResponse response) {
        // 서버 측 리프레시 토큰 무효화(삭제)
        refreshTokenRepository.deleteByUsername(username);

        ResponseCookie expiredAccessCookie = createCookie("accessToken", "", Duration.ZERO);
        ResponseCookie expiredRefreshCookie = createCookie("refreshToken", "", Duration.ZERO);

        response.addHeader(HttpHeaders.SET_COOKIE, expiredAccessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshCookie.toString());
    }

    private ResponseCookie createCookie(String name, String value, Duration duration) {
        return ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .maxAge(duration)
                .secure(true)
                .sameSite("None")
                .build();
    }
}