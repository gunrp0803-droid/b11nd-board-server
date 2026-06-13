package com.example.b11ndboard.commentlike.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // 데이터 조회를 위한 Getter 메서드들을 자동으로 생성합니다.
@NoArgsConstructor // JSON 데이터를 자바 객체로 파싱(역직렬화)할 때 필요한 기본 생성자를 자동으로 만듭니다.
public class CommentRequestDto {
    private String content; // 클라이언트가 입력한 댓글 내용이 담기는 필드입니다.
    private String writer;  // 클라이언트가 입력한 작성자 정보가 담기는 필드입니다.
}
