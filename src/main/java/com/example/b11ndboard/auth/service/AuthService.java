package com.example.b11ndboard.auth.service;

import com.example.b11ndboard.auth.jwt.JwtProvider;
import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.global.common.ResponseKind;
import com.example.b11ndboard.auth.dto.request.LoginRequest;
import com.example.b11ndboard.auth.entity.Users;
import com.example.b11ndboard.global.exception.LoginException;
import com.example.b11ndboard.auth.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.b11ndboard.global.exception.ErrorCode.LOGIN_FAILED;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<Void> login(LoginRequest request, HttpServletResponse response) {
        validateValue(request.username(), request.password());

        Users users = usersRepository.findByUsername(request.username())
                .orElseThrow(() -> new LoginException(LOGIN_FAILED));

        if (!passwordEncoder.matches(request.password(), users.getPassword())) {
            throw new LoginException(LOGIN_FAILED);
        }

        tokenService.generateTokens(users.getUsername(), users.getRole(), response);

        return ApiResponse.ok("로그인 성공", ResponseKind.LOGIN, null);
    }

    private void validateValue(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new LoginException(LOGIN_FAILED);
        }

        if (password == null || password.isBlank()) {
            throw new LoginException(LOGIN_FAILED);
        }
    }

    public ApiResponse<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtProvider.resolveRefreshToken(request);

        if (refreshToken == null || !jwtProvider.validateRefreshToken(refreshToken)) {
            throw new LoginException(LOGIN_FAILED);
        }

        String username = jwtProvider.getUsernameFromToken(refreshToken);

        Users users = usersRepository.findByUsername(username)
                .orElseThrow(() -> new LoginException(LOGIN_FAILED));

        tokenService.generateTokens(users.getUsername(), users.getRole(), response);

        return ApiResponse.ok("토큰 재발급 성공", ResponseKind.LOGIN, null);
    }

    public ApiResponse<Void> logout(Long userId, HttpServletResponse response) {
        tokenService.deleteTokens(userId, response);
        return ApiResponse.ok("로그아웃 성공", ResponseKind.LOGOUT, null);
    }
}