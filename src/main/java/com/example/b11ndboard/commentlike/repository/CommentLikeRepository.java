package com.example.b11ndboard.commentlike.repository;

import com.example.b11ndboard.commentlike.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike , Long>{

    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long CommentId);

    long countByCommentId(Long commentId);

}