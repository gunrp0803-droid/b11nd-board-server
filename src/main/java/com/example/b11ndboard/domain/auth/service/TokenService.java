package com.example.b11ndboard.domain.auth.service;

import com.example.b11ndboard.domain.auth.dto.response.TokenResponse;
import com.example.b11ndboard.domain.auth.entity.RefreshToken;
import com.example.b11ndboard.domain.user.entity.Role;
import com.example.b11ndboard.domain.auth.repository.RefreshTokenRepository;
import com.example.b11ndboard.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenResponse generateTokens(String username, Role role, HttpServletResponse response) {
        String accessToken = jwtProvider.generateAccessToken(username, role);
        String refreshToken = jwtProvider.generateRefreshToken(username, role);

        LocalDateTime expiryDate = LocalDateTime.now().plus(Duration.ofMillis(jwtProvider.getRefreshExpiration()));
        refreshTokenRepository.findByUsername(username)
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken, expiryDate),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .username(username)
                                        .token(refreshToken)
                                        .expiryDate(expiryDate)
                                        .build()
                        )
                );

        ResponseCookie accessCookie = createCookie("accessToken", accessToken, jwtProvider.getAccessExpiration());
        ResponseCookie refreshCookie = createCookie("refreshToken", refreshToken, jwtProvider.getRefreshExpiration());

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public void deleteTokens(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);
        if (refreshToken != null) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }

        ResponseCookie expiredAccessCookie = createCookie("accessToken", "", 0);
        ResponseCookie expiredRefreshCookie = createCookie("refreshToken", "", 0);

        response.addHeader(HttpHeaders.SET_COOKIE, expiredAccessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshCookie.toString());
    }

    public boolean isRefreshTokenExists(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(refreshToken -> refreshToken.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(false);
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