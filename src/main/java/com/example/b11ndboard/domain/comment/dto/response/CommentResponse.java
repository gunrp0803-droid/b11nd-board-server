package com.example.b11ndboard.domain.comment.dto.response;

import com.example.b11ndboard.domain.comment.entity.Comment;
import lombok.Getter;

//댓글 반환용
@Getter
public class CommentResponse {
    private Long id;
    private String content;
    private String writer;
    private String createdAt;

    public CommentResponse(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.writer = comment.getWriter();
        this.createdAt = comment.getCreatedAt().toString();

    }
}
