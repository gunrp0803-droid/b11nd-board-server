package com.example.b11ndboard.comment.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

//댓글 등록용
@Getter
@RequiredArgsConstructor
public class CommentRequest {
    private String Content;
    private String Writer;
}

