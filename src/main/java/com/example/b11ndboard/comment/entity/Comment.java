package com.example.b11ndboard.comment.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    //여러 댓글이 하나의 게시글에 속함
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id",nullable = false)
    private Board board;

    private String writer;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public Comment(String content , Board board , String writer){
        this.content = content;
        this.board = board;
        this.writer = writer;
    }


}
