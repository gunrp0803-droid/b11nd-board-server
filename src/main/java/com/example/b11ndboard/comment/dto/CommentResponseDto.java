package com.example.b11ndboard.comment.dto;

import com.example.b11ndboard.comment.entity.Comment;
import lombok.Getter;

//댓글 반환용
@Getter
public class CommentResponseDto{
    private Long id;
    private String content;
    private String writer;
    private String createdAt;

    public CommentResponseDto(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.writer = comment.getWriter();
        this.createdAt = comment.getCreatedAt().toString();

    }
}
