package com.example.b11ndboard.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    @NotBlank(message = "게시글 제목은 필수 입력 항목입니다.")
    @Size(max = 500, message = "제목은 500자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "게시글 내용은 필수 입력 항목입니다.")
    private String content;
}