package com.example.b11ndboard.domain.post.repository;

import com.example.b11ndboard.domain.post.entity.Post;
import com.example.b11ndboard.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserIdAndPost(Long userId, Post post);

    long countByPost(Post post);

    Optional<PostLike> findByUserIdAndPost(Long userId, Post post);

    @Query("select pl.post.id, count(pl) from PostLike pl where pl.post in :posts group by pl.post.id")
    List<Object[]> countLikesByPosts(@Param("posts") List<Post> posts);

    @Query("select pl.post.id from PostLike pl where pl.userId = :userId and pl.post in :posts")
    List<Long> findLikedPostIdsByUserIdAndPosts(@Param("userId") Long userId, @Param("posts") List<Post> posts);
}