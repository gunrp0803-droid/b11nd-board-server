package com.example.b11ndboard.comment.entity;

import com.example.b11ndboard.post.entity.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity //JPA ENTITY 임을 선언한다
@Getter //클래스 내부의 getter 를 자동으로 생성한다
@NoArgsConstructor //Lombok
public class Comment {
    @Id //테이블 에서 행을 식벽하는 고유 키임을 지정한다
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본키 생성을 DB 가 하며 저장시 id 가 1,2,3 순으로 자동으로 증가하게
    private Long id;

    @Column //데이터 베이스의 속성지정 속성 지정을 안하면 기본값 사용
    private String content;


    @ManyToOne(fetch = FetchType.LAZY) //여러 댓글이 하나의 게시글에 속함
    @JoinColumn(name = "post_id",nullable = false) //외래키를 지정 한다 , post_id 로 외래키를 지정하고 절대 null 일수 없게
    private Post post;

    private String writer;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder //보다 간편,보기 쉽게 만든다 getter setter 축소 버전,빌더 패턴 자동 구현
    public Comment(String content , Post post , String writer){
        this.content = content;
        this.post = post;
        this.writer = writer;
    }

    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }
        this.content = newContent;
    }

}
