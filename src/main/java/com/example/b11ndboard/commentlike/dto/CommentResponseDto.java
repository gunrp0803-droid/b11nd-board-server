package com.example.b11ndboard.commentlike.dto;

import com.example.b11ndboard.comment.entity.Comment;
import lombok.Getter;


@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String writer;
    private String createdAt;
    private long likeCount; // 💡 좋아요 개수 필드 추가

    // 생성자에 likeCount 매개변수 추가
    public CommentResponseDto(Comment comment, long likeCount) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.writer = comment.getWriter();
        this.createdAt = comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : "";
        this.likeCount = likeCount;
    }
}