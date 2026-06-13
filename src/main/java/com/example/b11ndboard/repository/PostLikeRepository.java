package com.example.b11ndboard.repository;

import com.example.b11ndboard.post.entity.Post;
import com.example.b11ndboard.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    // 유저 ID와 게시글 엔티티로 좋아요 존재 여부를 확인하는 메서드
    boolean existsByUserIdAndPost(Long userId, Post post);

    long countByPost(Post post);
}