package com.example.b11ndboard.post.repository;

import com.example.b11ndboard.post.entity.Post;
import com.example.b11ndboard.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    // 유저 ID와 게시글 엔티티로 좋아요 엔티티를 찾는 메서드
    Optional<PostLike> findByUserIdAndPost(Long userId, Post post);

    boolean existsByUserIdAndPost(Long userId, Post post);

    long countByPost(Post post);
}