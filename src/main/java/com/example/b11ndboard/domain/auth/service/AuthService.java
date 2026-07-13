package com.example.b11ndboard.domain.auth.service;

import com.example.b11ndboard.domain.auth.dto.request.LoginRequest;
import com.example.b11ndboard.domain.auth.dto.response.TokenResponse;
import com.example.b11ndboard.domain.auth.entity.RefreshToken;
import com.example.b11ndboard.domain.auth.repository.RefreshTokenRepository;
import com.example.b11ndboard.domain.user.dto.request.SignUpRequest;
import com.example.b11ndboard.global.jwt.JwtProvider;
import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.global.common.ResponseKind;
import com.example.b11ndboard.domain.user.entity.Users;
import com.example.b11ndboard.global.exception.LoginException;
import com.example.b11ndboard.domain.user.repository.UsersRepository;
import com.example.b11ndboard.global.exception.SignUpException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.b11ndboard.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;
    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ApiResponse<Void> signUp(SignUpRequest request) {
        if (usersRepository.existsByUsername(request.username())) {
            throw new SignUpException(SIGNUP_USERNAME_USED);
        }

        if (usersRepository.existsByEmail(request.email())) {
            throw new SignUpException(SIGNUP_EMAIL_USED);
        }

        Users users = Users.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build();

        usersRepository.save(users);

        return ApiResponse.ok("회원가입 성공", ResponseKind.SIGNUP, null);
    }

    @Transactional
    public ApiResponse<TokenResponse> login(LoginRequest request, HttpServletResponse response) {
        Users users = usersRepository.findByUsername(request.username())
                .orElseThrow(() -> new LoginException(LOGIN_FAILED));

        if (!passwordEncoder.matches(request.password(), users.getPassword())) {
            throw new LoginException(LOGIN_FAILED);
        }

        TokenResponse tokenResponse = tokenService.generateTokens(users.getUsername(), users.getRole(), response);

        return ApiResponse.ok("로그인 성공", ResponseKind.LOGIN, tokenResponse);
    }

    @Transactional
    public ApiResponse<TokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        if (refreshToken == null || !jwtProvider.validateRefreshToken(refreshToken)) {
            throw new LoginException(LOGIN_FAILED);
        }

        // DB 검증: 로그아웃된 토큰인지 확인
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new LoginException(LOGIN_FAILED));

        String username = jwtProvider.getUsernameFromToken(refreshToken);

        Users users = usersRepository.findByUsername(username)
                .orElseThrow(() -> new LoginException(LOGIN_FAILED));

        // 기존 사용된 리프레시 토큰 폐기 (Refresh Token Rotation)
        refreshTokenRepository.delete(storedToken);

        TokenResponse tokenResponse = tokenService.generateTokens(users.getUsername(), users.getRole(), response);

        return ApiResponse.ok("토큰 재발급 성공", ResponseKind.LOGIN, tokenResponse);
    }

    @Transactional
    public ApiResponse<Void> logout(Long userId, HttpServletResponse response) {
        Users users = usersRepository.findById(userId)
                .orElseThrow(() -> new LoginException(LOGIN_FAILED));

        tokenService.deleteTokens(users.getUsername(), response);
        return ApiResponse.ok("로그아웃 성공", ResponseKind.LOGOUT, null);
    }
}