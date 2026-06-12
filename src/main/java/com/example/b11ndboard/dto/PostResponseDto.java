package com.example.b11ndboard.dto;

import com.example.b11ndboard.entity.Post;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long likeCount;     // 이 글의 총 좋아요 개수
    private boolean liked;      // 현재 로그인한 사용자가 좋아요를 눌렀는지 여부

    // 기존 단건 엔티티 변환용 생성자 (기본값 설정)
    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userId = post.getUserId();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.likeCount = 0;
        this.liked = false;
    }

    public PostResponseDto(Post post, long likeCount, boolean liked) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userId = post.getUserId();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.likeCount = likeCount;
        this.liked = liked;
    }
}