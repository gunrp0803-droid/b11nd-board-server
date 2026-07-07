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
    private Boolean isWriter; // 현재 로그인한 사용자가 이 댓글의 작성자인지 여부

    // 생성자에 likeCount 및 isWriter 매개변수 추가
    public CommentResponseDto(Comment comment, long likeCount, boolean isWriter) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.writer = comment.getWriter();
        this.createdAt = comment.getCreatedAt().toString();
        this.likeCount = likeCount;
        this.isWriter = isWriter;
    }
}