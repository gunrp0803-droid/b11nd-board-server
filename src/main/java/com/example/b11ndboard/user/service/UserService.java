package com.example.b11ndboard.user.service;

import com.example.b11ndboard.auth.dto.request.SignUpRequest;
import com.example.b11ndboard.auth.entity.Users;
import com.example.b11ndboard.auth.repository.UsersRepository;
import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.global.common.ResponseKind;
import com.example.b11ndboard.global.exception.SignUpException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.b11ndboard.global.exception.ErrorCode.SIGNUP_EMAIL_USED;
import static com.example.b11ndboard.global.exception.ErrorCode.SIGNUP_USERNAME_USED;

@Service
@RequiredArgsConstructor
public class UserService {
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
}