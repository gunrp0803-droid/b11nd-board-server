package com.example.b11ndboard.domain.comment.repository;

import com.example.b11ndboard.domain.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike , Long>{

    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long CommentId);

    boolean existsByUserIdAndCommentId(Long userId, Long commentId);

    long countByCommentId(Long commentId);

}