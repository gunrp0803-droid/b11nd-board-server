package com.example.b11ndboard.comment.entity;

import com.example.b11ndboard.global.entity.BaseTimeEntity;
import com.example.b11ndboard.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String writer;

    @Builder
    public Comment(String content, Post post, String writer) {
        this.content = content;
        this.post = post;
        this.writer = writer;
    }

    public void update(String content) {
        this.content = content;
    }
}
