package com.example.b11ndboard.comment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

//댓글 등록용
@Getter
@RequiredArgsConstructor
public class CommentRequestDto{
    private String Content;
    private String Writer;
}

