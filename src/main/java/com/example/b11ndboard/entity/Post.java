package com.example.b11ndboard.entity;

import com.example.b11ndboard.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    //JWT 완성하기 전까지는 임시로 숫자를 넣거나 static하게 검증할 작성자 ID
    @Column(nullable = false)
    private Long userId;

    @Builder
    public Post(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;

    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}