package com.example.b11ndboard.auth.service;

import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.global.common.ResponseKind;
import com.example.b11ndboard.auth.dto.request.LoginRequest;
import com.example.b11ndboard.auth.entity.Users;
import com.example.b11ndboard.global.exception.LoginException;
import com.example.b11ndboard.auth.repository.UsersRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.b11ndboard.global.exception.ErrorCode.LOGIN_FAILED;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenService tokenService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<String> login(LoginRequest request, HttpServletResponse response) {
        validateValue(request.username(), request.password());

        Users users = usersRepository.findByUsername(request.username())
                .orElseThrow(() -> new LoginException(LOGIN_FAILED));

        if (!passwordEncoder.matches(request.password(), users.getPassword())) {
            throw new LoginException(LOGIN_FAILED);
        }

        String accessToken = tokenService.generateAccessToken(
                users.getUsername(),
                users.getRole(),
                response
        );

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
}