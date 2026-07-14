package com.example.b11ndboard.domain.auth.service;

import com.example.b11ndboard.domain.auth.dto.request.LoginRequest;
import com.example.b11ndboard.domain.auth.dto.response.TokenResponse;
import com.example.b11ndboard.global.jwt.JwtProvider;
import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.global.common.ResponseKind;
import com.example.b11ndboard.domain.user.entity.Users;
import com.example.b11ndboard.global.exception.LoginException;
import com.example.b11ndboard.domain.user.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.b11ndboard.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;



    public ApiResponse<TokenResponse> login(LoginRequest request, HttpServletResponse response) {
        Users users = usersRepository.findByUsername(request.username())
                .orElseThrow(() -> new LoginException(LOGIN_FAILED));

        if (!passwordEncoder.matches(request.password(), users.getPassword())) {
            throw new LoginException(LOGIN_FAILED);
        }

        TokenResponse tokenResponse = tokenService.generateTokens(users.getUsername(), users.getRole(), response);

        return ApiResponse.ok("로그인 성공", ResponseKind.LOGIN, tokenResponse);
    }

    public ApiResponse<TokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        if (refreshToken == null || !jwtProvider.validateRefreshToken(refreshToken)
                || !tokenService.isRefreshTokenExists(refreshToken)) {
            throw new LoginException(LOGIN_FAILED);
        }

        String username = jwtProvider.getUsernameFromToken(refreshToken);

        Users users = usersRepository.findByUsername(username)
                .orElseThrow(() -> new LoginException(LOGIN_FAILED));

        TokenResponse tokenResponse = tokenService.generateTokens(users.getUsername(), users.getRole(), response);

        return ApiResponse.ok("토큰 재발급 성공", ResponseKind.LOGIN, tokenResponse);
    }

    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        tokenService.deleteTokens(request, response);
        return ApiResponse.ok("로그아웃 성공", ResponseKind.LOGOUT);
    }
}