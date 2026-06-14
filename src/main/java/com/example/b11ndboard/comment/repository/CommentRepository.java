package com.example.b11ndboard.comment.repository;

import com.example.b11ndboard.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.b11ndboard.post.entity.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
