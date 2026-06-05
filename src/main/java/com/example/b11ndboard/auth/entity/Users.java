package com.example.b11ndboard.auth.entity;

import com.example.b11ndboard.auth.dto.request.SignUpRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @Column(length = 100)
    private String password;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Users toEntity(SignUpRequest request, String password) {
        return Users.builder()
                .username(request.username())
                .password(password)
                .email(request.email())
                .role(Role.USER)
                .build();
    }
}