package com.example.b11ndboard.domain.auth.service;

import com.example.b11ndboard.domain.auth.dto.response.TokenResponse;
import com.example.b11ndboard.domain.user.entity.Role;
import com.example.b11ndboard.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    public TokenResponse generateTokens(String username, Role role, HttpServletResponse response) {
        String accessToken = jwtProvider.generateAccessToken(username, role);
        String refreshToken = jwtProvider.generateRefreshToken(username, role);

        redisTemplate.opsForValue().set(
                "RT:" + refreshToken,
                username,
                Duration.ofMillis(jwtProvider.getRefreshExpiration())
        );

        ResponseCookie accessCookie = createCookie("accessToken", accessToken, jwtProvider.getAccessExpiration());
        ResponseCookie refreshCookie = createCookie("refreshToken", refreshToken, jwtProvider.getRefreshExpiration());

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return new TokenResponse(accessToken, refreshToken);
    }

    public void deleteTokens(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        if (refreshToken != null) {
            redisTemplate.delete("RT:" + refreshToken);
        }

        ResponseCookie expiredAccessCookie = createCookie("accessToken", "", 0);
        ResponseCookie expiredRefreshCookie = createCookie("refreshToken", "", 0);

        response.addHeader(HttpHeaders.SET_COOKIE, expiredAccessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshCookie.toString());
    }

    public boolean isRefreshTokenExists(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("RT:" + token));
    }

    private ResponseCookie createCookie(String name, String value, long maxAgeMillis) {
        return ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .maxAge(Duration.ofMillis(maxAgeMillis))
                .secure(true)
                .sameSite("None")
                .build();
    }
}