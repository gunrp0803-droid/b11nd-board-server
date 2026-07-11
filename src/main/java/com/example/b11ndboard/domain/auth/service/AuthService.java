package com.example.b11ndboard.domain.auth.service;

import com.example.b11ndboard.domain.auth.dto.request.LoginRequest;
import com.example.b11ndboard.domain.auth.dto.request.SignUpRequest;
import com.example.b11ndboard.global.jwt.JwtProvider;
import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.global.common.ResponseKind;
import com.example.b11ndboard.domain.auth.entity.Users;
import com.example.b11ndboard.global.exception.LoginException;
import com.example.b11ndboard.domain.auth.repository.UsersRepository;
import com.example.b11ndboard.global.exception.SignUpException;
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

    public ApiResponse<Void> signUp(SignUpRequest request) throws SignUpException {
        if (usersRepository.existsByUsername(request.username())) {
            throw new SignUpException(SIGNUP_USERNAME_USED);
        }

        if (usersRepository.existsByEmail(request.email())) {
            throw new SignUpException(SIGNUP_EMAIL_USED);
        }

        usersRepository.save(Users.toEntity(request, passwordEncoder.encode(request.password())));
        return ApiResponse.ok("회원가입에 성공했습니다.", ResponseKind.SIGNUP, null);
    }

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